import React, {useEffect, useState} from 'react';
import BackendService from '../services/BackendService';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft, faSave} from '@fortawesome/fontawesome-free-solid';
import {alertActions} from "../utils/Rdx";
import {connect} from "react-redux";
import {Form} from "react-bootstrap";
import {useNavigate, useParams} from "react-router-dom";

const UserComponent = props => {

    const params = useParams();

    const [id, setId] = useState(params.id);
    const [login, setLogin] = useState("");
    const [email, setEmail] = useState("");
    const [hidden, setHidden] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        if (parseInt(id) !== -1) {
            BackendService.retrieveUser(id)
                .then((resp) => {
                    setLogin(resp.data.login)
                    setEmail(resp.data.email)
                })
                .catch(() => setHidden(true))
        }
    }, []);

    const onSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        let err = null;
        if (!login) err = "Логин должен быть указан";
        if (!email) err = "Е-майл должен быть указан";
        if (err) props.dispatch(alertActions.error(err));
        let user = {id, login, email};

        if (parseInt(user.id) === -1) {
            BackendService.createUser(user)
                .then(() => navigate(`/users`))
                .catch(() => {})
        } else {
            BackendService.updateUser(user)
                .then(() => navigate(`/users`))
                .catch(() => {})
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className=" row my-2 mr-0">
                <h3>Пользователь</h3>
                <button className="btn btn-outline-secondary ml-auto"
                        onClick={() => navigate(`/users`)}
                ><FontAwesomeIcon icon={faChevronLeft}/>{' '}Назад</button>
            </div>
            <Form onSubmit={onSubmit}>
                <Form.Group>
                    <Form.Label>Логин</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите логин"
                        onChange={(e) => {setLogin(e.target.value)}}
                        value={login}
                        name="login"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>E-mail</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите e-mail"
                        onChange={(e) => {setEmail(e.target.value)}}
                        value={email}
                        name="email"
                        autoComplete="off"
                    />
                </Form.Group>
                <button className="btn btn-outline-secondary" type="submit">
                    <FontAwesomeIcon icon={faSave}/>{' '}
                    Сохранить
                </button>
            </Form>
        </div>
    );
};

export default connect()(UserComponent);