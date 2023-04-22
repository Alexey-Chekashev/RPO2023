
import './App.css';
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import {createBrowserHistory} from "history";
import Login from "./components/Login";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";

function App() {
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar />
                <div className="container-fluid">
                    <Routes>
                        <Route path="home" element={<Home />} />
                        <Route path="login" element={<Login />} />
                    </Routes>
                </div>
            </BrowserRouter>
        </div>
    );
}

