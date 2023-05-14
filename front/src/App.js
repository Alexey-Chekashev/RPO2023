import './App.css';
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {connect} from "react-redux";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";
import Utils from "./utils/Utils";
import {useState} from "react";
import SideBar from "./components/SideBar";
import CountryListComponent from "./components/CountryListComponent";
import CountryComponent from "./components/CountryComponent";
import ArtistListComponent from "./components/ArtistListComponent";
import ArtistComponent from "./components/ArtistComponent";
import PaintingListComponent from "./components/PaintingListComponent";
import MuseumListComponent from "./components/MuseumListComponent";
import UserListComponent from "./components/UserListComponent";
import MuseumComponent from "./components/MuseumComponent";
import PaintingComponent from "./components/PaintingComponent";
import UserComponent from "./components/UserComponent";
import MyAccountComponent from "./components/MyAccountComponent";
const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    return user ? children : <Navigate to={'/login'} />
};

const App = props => {
    const [exp,setExpanded] = useState(true);
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar toggleSideBar={() =>
                    setExpanded(!exp)}/>
                <div className="wrapper">
                    <SideBar expanded={exp} />
                    <div className="container-fluid">
                        { props.error_message &&  <div className="alert alert-danger m-1">{props.error_message}</div>}
                        <Routes>
                            <Route path="login" element={<Login />}/>
                            <Route path="home" element={<ProtectedRoute><Home/></ProtectedRoute>}/>
                            <Route path="countries" element={<ProtectedRoute> <CountryListComponent/> </ProtectedRoute>}/>
                            <Route path="countries/:id" element={<ProtectedRoute><CountryComponent/></ProtectedRoute>}/>
                            <Route path="artists" element={<ProtectedRoute><ArtistListComponent/></ProtectedRoute>}/>
                            <Route path="artists/:id" element={<ProtectedRoute><ArtistComponent /></ProtectedRoute>}/>
                            <Route path="paintings" element={<ProtectedRoute><PaintingListComponent/></ProtectedRoute>}/>
                            <Route path="paintings/:id" element={<ProtectedRoute><PaintingComponent /></ProtectedRoute>}/>
                            <Route path="museums" element={<ProtectedRoute><MuseumListComponent/></ProtectedRoute>}/>
                            <Route path="museums/:id" element={<ProtectedRoute><MuseumComponent /></ProtectedRoute>}/>
                            <Route path="users" element={<ProtectedRoute><UserListComponent/></ProtectedRoute>}/>
                            <Route path="users/:id" element={<ProtectedRoute><UserComponent /></ProtectedRoute>}/>
                            <Route path="account" element={<ProtectedRoute><MyAccountComponent /></ProtectedRoute>}/>
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </div>
    );
}

function mapStateToProps(state) {
    const { msg } = state.alert;
    return { error_message: msg };
}

export default connect(mapStateToProps)(App);