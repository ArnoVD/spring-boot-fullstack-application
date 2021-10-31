import { useState, useEffect } from 'react';
import { getAllStudents } from "./client";
import {Layout, Menu, Breadcrumb, Table, Spin, Empty} from 'antd';
import {
    DesktopOutlined,
    PieChartOutlined,
    FileOutlined,
    TeamOutlined,
    UserOutlined,
    LoadingOutlined,
} from '@ant-design/icons';

import './App.css';

const { Header, Content, Footer, Sider } = Layout;
const { SubMenu } = Menu;

const columns = [
    {
        title: 'Id',
        dataIndex: 'id',
        key: 'id',
    },
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
    },
    {
        title: 'Email',
        dataIndex: 'email',
        key: 'email',
    },
    {
        title: 'Gender',
        dataIndex: 'gender',
        key: 'gender',
    }
];

const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;

function App() {
    // useState is used for managing the state of the application
    const [students, setStudents] = useState([]);
    const [collapsed, setCollapsed] = useState(false);
    const [fetching, setFetching] = useState(true);

    const fetchStudents = () =>
        getAllStudents()
            .then(res => res.json())
            .then(data => {
                console.log(data);
                setStudents(data);
                // Stop the loading icon
                setFetching(false);
            });

    // useEffect is used to run the functions inside once the component is mounted
    useEffect(() => {
        console.log("Component is mounted")
        fetchStudents();
    }, []);

    const renderStudents = () => {
        // While we are fetching the data we return a animated spinning icon
        if(fetching) {
            return <div className="loadingSpinner"><Spin indicator={antIcon} /></div>
        }

        // If there are no student, we return a empty box icon
        if(students.length <= 0){
            return <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />;
        }

        return <Table
            dataSource={students}
            columns={columns}
            bordered
            title={() => 'Students'}
            pagination={{ pageSize: 50 }}
            scroll={{ y: 240 }}
            // This indexes every row of the table we return (Otherwise we get errors in our console)
            rowKey = {(student) => student.id}
        />;
    }

    return <Layout style={{ minHeight: '100vh' }}>
        <Sider collapsible collapsed={collapsed}
               onCollapse={setCollapsed}>
            <div className="logo" />
            <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                <Menu.Item key="1" icon={<PieChartOutlined />}>
                    Option 1
                </Menu.Item>
                <Menu.Item key="2" icon={<DesktopOutlined />}>
                    Option 2
                </Menu.Item>
                <SubMenu key="sub1" icon={<UserOutlined />} title="User">
                    <Menu.Item key="3">User 1</Menu.Item>
                    <Menu.Item key="4">User 2</Menu.Item>
                    <Menu.Item key="5">User 3</Menu.Item>
                </SubMenu>
                <SubMenu key="sub2" icon={<TeamOutlined />} title="Team">
                    <Menu.Item key="6">Team 1</Menu.Item>
                    <Menu.Item key="8">Team 2</Menu.Item>
                </SubMenu>
                <Menu.Item key="9" icon={<FileOutlined />}>
                    Files
                </Menu.Item>
            </Menu>
        </Sider>
        <Layout className="site-layout">
            <Header className="site-layout-background" style={{ padding: 0 }} />
            <Content style={{ margin: '0 16px' }}>
                <Breadcrumb style={{ margin: '16px 0' }}>
                    <Breadcrumb.Item>Students</Breadcrumb.Item>
                    <Breadcrumb.Item>All students</Breadcrumb.Item>
                </Breadcrumb>
                <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
                    {renderStudents()}
                </div>
            </Content>
            <Footer style={{ textAlign: 'center' }}>By Arno Vandijck ©2021</Footer>
        </Layout>
    </Layout>
}

export default App;
