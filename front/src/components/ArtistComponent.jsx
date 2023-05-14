import React, {useEffect, useState} from 'react';
import BackendService from '../services/BackendService';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft, faSave} from '@fortawesome/fontawesome-free-solid';
import {alertActions} from "../utils/Rdx";
import {connect} from "react-redux";
import {Form} from "react-bootstrap";
import {useNavigate, useParams} from "react-router-dom";
import {faEdit} from "@fortawesome/free-solid-svg-icons";

const ArtistComponent = props => {

    const params = useParams();

    const [id, setId] = useState(params.id);
    const [name, setName] = useState("");
    const [age, setAge] = useState("");
    const [country, setCountry] = useState({});
    const [hidden, setHidden] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        if (parseInt(id) !== -1) {
            BackendService.retrieveArtist(id)
                .then((resp) => {
                    setName(resp.data.name)
                    setAge(resp.data.age)
                    setCountry(resp.data.country)
                })
                .catch(() => setHidden(true))
        }
    }, []);

    const changeCountry = (e) => {
        setCountry({name: e.target.value});
        console.log(e.target.value);
    }

    const onSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        let err = null;
        if (!name) err = "Имя художника должно быть указано";
        if (!age) err = "Век должен быть указан";
        if (err) props.dispatch(alertActions.error(err));
        let artist = {id, name, country, age};

        if (parseInt(artist.id) === -1) {
            BackendService.createArtist(artist)
                .then(() => navigate(`/artists`))
                .catch(() => {})
        } else {
            BackendService.updateArtist(artist)
                .then(() => navigate(`/artists`))
                .catch(() => {})
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className=" row my-2 mr-0">
                <h3>Художник</h3>
                <button className="btn btn-outline-secondary ml-auto"
                        onClick={() => navigate(`/artists`)}
                ><FontAwesomeIcon icon={faChevronLeft}/>{' '}Назад</button>
            </div>
            <Form onSubmit={onSubmit}>
                <Form.Group>
                    <Form.Label>Имя</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите имя художника"
                        onChange={(e) => {setName(e.target.value)}}
                        value={name}
                        name="name"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Возраст</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите возраст"
                        onChange={(e) => {setAge(e.target.value)}}
                        value={age}
                        name="age"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Страна</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите название страны"
                        onChange={(e) => {changeCountry(e)}}
                        value={country.name}
                        name="country"
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

export default connect()(ArtistComponent);