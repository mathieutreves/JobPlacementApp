import {Navbar, Container, Nav} from "react-bootstrap";
import {MeInterface} from "../App.tsx";

function NavHeader({me}: { me: MeInterface | null }) {
    return (
        <Navbar bg="dark" variant="dark" className="bg-gray">
            <Container fluid>
                <Navbar.Brand className="fs-2">WA2 lab</Navbar.Brand>
                <Navbar.Toggle/>
                <Navbar.Collapse className="justify-content-end">
                    <Nav className="ms-auto align-items-center">
                        {me && me.principal ? (
                            <>
                                <Nav.Item className="d-flex align-items-center">
                                    <Navbar.Text className="fs-5 me-3">
                                        Signed in as: {me.name}
                                    </Navbar.Text>
                                </Nav.Item>
                                <Nav.Item>
                                    <form method="post" action={me.logoutUrl}>
                                        <input type="hidden" name="_csrf" value={me.xsrfToken}/>
                                        <button type="submit" className="btn btn-danger">
                                            Logout
                                        </button>
                                    </form>
                                </Nav.Item>
                            </>
                        ) : (
                            me && me.loginUrl && (
                                <Nav.Item>
                                    <button
                                        onClick={() => (window.location.href = "http://localhost:8088/login".concat("", me.loginUrl))}
                                        className="btn btn-warning"
                                    >
                                        Login
                                    </button>
                                </Nav.Item>
                            )
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavHeader;
