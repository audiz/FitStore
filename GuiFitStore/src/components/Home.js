import React,{Component} from 'react';
import {fetchHandler} from "./scripts/fetchHandler";
import ErrorAlerts from "./alerts/ErrorAlerts";
import {Link} from "react-router-dom";
import axios from 'axios';

class Home extends Component {
    apiPath = '/api/summary';

    constructor() {
        super();
        this.state = { object: false,  selectedFile: null, response: null };
    }

    componentDidMount() {
        document.title = 'Home';
        this.refresh();
    }

    // On file select (from the pop up)
    onFileChange = event => {
        // Update the state
        this.setState({ selectedFile: event.target.files[0], response: null });
    };

    // On file upload (click the upload button)
    onFileUpload = () => {
        // Create an object of formData
        const formData = new FormData();

        // Update the formData object
        formData.append(
            "file",
            this.state.selectedFile,
            this.state.selectedFile.name
        );

        // Details of the uploaded file
        console.log(this.state.selectedFile);

        // Request made to the backend api
        // Send formData object
        axios.post("api/reader/uploadFile", formData)
            .then(response => this.setState({ response: response.data }));
    };

    // File content to be displayed after
    // file upload is complete
    fileData = () => {
        if (this.state.selectedFile) {

            return (
                <div>
                    <h2>File Details:</h2>
                    <p>File Name: {this.state.selectedFile.name}</p>
                    <p>File Type: {this.state.selectedFile.type}</p>
                    <p>
                        Last Modified:{" "}
                        {this.state.selectedFile.lastModifiedDate !== undefined ? this.state.selectedFile.lastModifiedDate.toDateString(): null}
                    </p>
                </div>
            );
        } else {
            return (
                <div>
                    <br />
                    <h4>Choose before Pressing the Upload button</h4>
                </div>
            );
        }
    };

    refresh() {
        fetchHandler(this.apiPath, null,(json) => {
            this.setState({object: json})
        }, (json) =>  {
            if(json && json.code !== undefined && json.message !== undefined) {
                this.setState({errorMsg: json, hasErrors: true})
            }
        })
    }

    render() {
        let sum = Object.keys(this.state.object).reduce((sum,key)=>sum+parseFloat(this.state.object[key]||0),0);
        return(

                <div class="container">
                    <div>
                        <h3>
                            Upload Fit File
                        </h3>
                        <div>
                            <input type="file" onChange={this.onFileChange} />
                            <button onClick={this.onFileUpload}>
                                Upload!
                            </button>
                        </div>
                        {this.fileData()}
                        {this.state.response}
                    </div>
                </div>
        );
    }
}
export default Home;