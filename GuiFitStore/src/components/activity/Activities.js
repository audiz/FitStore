import React,{Component} from 'react';
import {BrowserRouter as Router, Link, Switch, Route, useParams, useRouteMatch} from 'react-router-dom';
import TrainingsTable from "./ActivitiesTable";
import ActivityInfo from "./ActivityInfo";
import { withRouter } from "react-router";

class Activities extends Component {
    componentDidMount() {
        document.title = 'TrainingsTable'
    }

    render() {
        const { match, location, history } = this.props;
        return ( <div>
            <Switch>
                <Route path="/Activities" exact={true} component={TrainingsTable} />
                <Route path="/Activities/:topicId" exact={true} component={ActivityInfo} ></Route>
            </Switch>

        </div>)
    }
}
export default withRouter(Activities);