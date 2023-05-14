import React, {useEffect, useState} from 'react';
import BackendService from '../services/BackendService';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faChevronLeft, faSave} from '@fortawesome/fontawesome-free-solid';
import {alertActions} from "../utils/Rdx";
import {connect} from "react-redux";
import {Form} from "react-bootstrap";
import {useNavigate, useParams} from "react-router-dom";
import {faEdit} from "@fortawesome/free-solid-svg-icons";

const PaintingComponent = props => {

    const params = useParams();

    const [id, setId] = useState(params.id);
    const [name, setName] = useState("");
    const [artistid, setArtistid] = useState({});
    const [museumid, setMuseumid] = useState({});
    const [year, setYear] = useState("");
    const [hidden, setHidden] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        if (parseInt(id) !== -1) {
            BackendService.retrievePainting(id)
                .then((resp) => {
                    setName(resp.data.name);
                    setArtistid(resp.data.artistid);
                    setMuseumid(resp.data.museumid);
                    setYear(resp.data.year);
                })
                .catch(() => setHidden(true))
        }
    }, []);

    const changeArtist = (e) => {
        setArtistid({name: e.target.value});
        console.log(e.target.value);
    }

    const changeMuseum = (e) => {
        setMuseumid({name: e.target.value});
        console.log(e.target.value);
    }

    const onSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        let err = null;
        if (!name) err = "Название картины должно быть указано";
        if (!year) err = "Года создания должен быть указан";
        if (err) props.dispatch(alertActions.error(err));
        let painting = {id, name, artistid, museumid, year};

        if (parseInt(painting.id) === -1) {
            BackendService.createPainting(painting)
                .then(() => navigate(`/paintings`))
                .catch(() => {})
        } else {
            BackendService.updatePainting(painting)
                .then(() => navigate(`/paintings`))
                .catch(() => {})
        }
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className=" row my-2 mr-0">
                <h3>Картина</h3>
                <button className="btn btn-outline-secondary ml-auto"
                        onClick={() => navigate(`/paintings`)}
                ><FontAwesomeIcon icon={faChevronLeft}/>{' '}Назад</button>
            </div>
            <Form onSubmit={onSubmit}>
                <Form.Group>
                    <Form.Label>Название</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите название картины"
                        onChange={(e) => {setName(e.target.value)}}
                        value={name}
                        name="name"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Художник</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите имя художника"
                        onChange={(e) => {changeArtist(e)}}
                        value={artistid.name}
                        name="artistid"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Музей</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите название музея"
                        onChange={(e) => {changeMuseum(e)}}
                        value={museumid.name}
                        name="museumid"
                        autoComplete="off"
                    />
                </Form.Group>
                <Form.Group>
                    <Form.Label>Год создания</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Введите год"
                        onChange={(e) => {setYear(e.target.value)}}
                        value={year}
                        name="year"
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

export default connect()(PaintingComponent);