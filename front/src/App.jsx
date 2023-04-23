
import './App.css';
import {BrowserRouter, Routes, Route, Navigate} from "react-router-dom";
import {connect} from "react-redux";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";
import Utils from "./utils/Utils";
const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    return user ? children : <Navigate to={'/login'} />
}

function App(props) {
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar />
                <div className="container-fluid">
                    {props.error_message &&
                        <div className="alert alert-danger m-1">{props.error_message}</div>}
                    <Routes>
                        <Route path="home" element={<ProtectedRoute><Home/></ProtectedRoute>}/>
                        <Route path="login" element={<Login />} />
                    </Routes>
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