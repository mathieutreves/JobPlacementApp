import {Alert} from "react-bootstrap";

function ContactView({data, getData, errorMsg, setErrorMsg}: { data: string, getData: () => void, errorMsg: string, setErrorMsg: React.Dispatch<React.SetStateAction<string>>}) {

    return (
        <>
            {errorMsg && <Alert variant='danger' onClose={() => setErrorMsg('')} dismissible>{errorMsg}</Alert>}
            <div style={{
                border: "dashed 1px gray",
                padding: "1em",
                marginTop: "2em",
                marginBottom: "1em",
                maxHeight: "400px",
                overflowY: "auto",
            }}>
            <pre className="m-0">
                {JSON.stringify(data, null, 4)}
            </pre>
            </div>
            <button
                onClick={getData}
                className="btn btn-dark"
            >
                Get Contacts
            </button>
        </>
    );
}

export default ContactView;
