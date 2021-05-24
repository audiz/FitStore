import React, { Component } from 'react';
import GoogleMapReact from "google-map-react";

class GoogleMaps extends Component {

    _marker;

    setMarker = (obj) => {
        //console.log(obj);
        this._marker.setPosition(obj);
    };

    loadMap = (map, maps) => {
        console.log("Load map");

        this._map = map;
        this._maps = maps;
        /*const cityCircle = new google.maps.Circle({
            strokeColor: "#FF0000",
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: "#FF0000",
            fillOpacity: 0.35,
            map,
            center: { lat: 40.756795, lng: -73.954298 },
            radius: 10000,
            draggable: true
        });*/

        this._marker = new google.maps.Marker({
            position: this.props.trackData[this.props.trackData.length - 1],
            map,
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                fillColor: '#00F',
                fillOpacity: 0.6,
                strokeColor: '#00A',
                strokeOpacity: 0.9,
                strokeWeight: 1,
                scale: 4
            }
        });


        /*this._marker = new maps.Marker({
            position: this.props.trackData[this.props.trackData.length - 1],
            map,
            draggable:false,
        });
*/
        const flightPath = new google.maps.Polyline({
            path: this.props.trackData,
            geodesic: true,
            strokeColor: "#000000",
            strokeOpacity: 1.0,
            strokeWeight: 2,
            map
        });

        map.fitBounds(this.props.bounds);


    };
    render() {

        return (
            <div>
            {this.props.trackData.length > 0 ? <div style={{ height: "250px", width: "100%" }}>
                    <GoogleMapReact
                        bootstrapURLKeys={{ key: "google_api_key" }}
                        defaultCenter={this.props.trackData[this.props.trackData.length - 1]}
                        defaultZoom={10}
                        yesIWantToUseGoogleMapApiInternals
                        onGoogleApiLoaded={({ map, maps }) => this.loadMap(map, maps)}
                    />
                </div> : ""}
            </div>
        );
    }
}

export default GoogleMaps;