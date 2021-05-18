import React from 'react'

import {
    BrowserRouter as Router,
    Switch,
    Route,
    Redirect,
    Link,
    useParams, useRouteMatch,
} from "react-router-dom";
import { Navbar,Nav } from 'react-bootstrap'
import Home from './Home';
import Activities from './activity/Activities';
import Segments from './segment/Segments';

import 'bootstrap/dist/css/bootstrap.min.css';

class BootstrapNavbar extends React.Component {

    constructor() {
        super();
    }

    componentDidMount() {

    }

    render() {
        return(
            <div>
                <Router>
                    <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
                        <Navbar.Toggle aria-controls="basic-navbar-nav" />
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="mr-auto">
                                <Nav.Link href="/">Home</Nav.Link>
                                <Nav><Link className="nav-link" to="/Activities">Activities</Link></Nav>
                                <Nav><Link className="nav-link" to="/Segments">Segments</Link></Nav>
                            </Nav>
                        </Navbar.Collapse>
                    </Navbar>
                    <br />

                    <Switch>
                        <Route exact path="/" component={Home} />
                        <Route path="/Activities" component={Activities} />
                        <Route path="/Segments" component={Segments} />
                    </Switch>
                </Router>
            </div>
        )
    }
}

export default BootstrapNavbar;