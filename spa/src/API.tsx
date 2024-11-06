import {MeInterface} from "./App.tsx";
import {Contact} from "./component/ContactForm.tsx";

const URL = 'http://localhost:8088/api/v1/API';

async function sendData(me: MeInterface | null, data: Contact) {
    try {
        const response = await fetch(`${URL}/contacts?name=${data.name}&surname=${data.surname}`, {
            method: 'POST',
            headers: {
                "X-XSRF-TOKEN": me ? me.xsrfToken : ""
            }
        });
        console.log(response)
        if (response.status == 401)
            return "Not authorized";

        return response;
    } catch (error) {
        console.log(error)
        throw {error: error || "Cannot parse server response."};
    }
}

async function getData() {
    try {
        const response = await fetch(`${URL}/contacts`);
        const responseBody = await response.json();
        if (response.status == 401)
            return "Not authorized";

        return responseBody;
    } catch (error) {
        throw {error: error || "Cannot fetch data."};
    }
}

const API = {sendData, getData};
export default API;
