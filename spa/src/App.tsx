import 'bootstrap/dist/css/bootstrap.min.css';
import {useEffect, useState} from 'react';
import NavHeader from './component/NavbarComponent.tsx';
import {Container} from "react-bootstrap";
import PrincipalView from "./component/PrincipalView.tsx";
import './App.css'

export interface MeInterface {
    name: string,
    loginUrl: string,
    logoutUrl: string,
    principal: never | null,
    xsrfToken: string
}

function App() {
    const [me, setMe] = useState<MeInterface | null>(null);

    useEffect(() => {
        const fetchMe = async () => {
            try {
                const res = await fetch('http://localhost:8088/me');
                const meData = await res.json() as MeInterface;
                setMe(meData);
            } catch (err) {
                setMe(null);
            }
        };

        fetchMe().then().catch((error) => {
            console.error("Failed to fetch data:", error);
        });
    }, []);

    return (
        <>
            <NavHeader me={me}/>
            <Container fluid className="vh-100 justify-content-center align-items-center bg-light">
                {me && me.principal && (
                    <PrincipalView principal={me.principal}/>
                )}
            </Container>
        </>
    );
}

export default App;
