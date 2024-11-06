import {Container, Card, Badge, Stack} from "react-bootstrap";

interface Principal {
    idToken: {
        tokenValue: string;
    };
    userInfo: {
        claims: {
            name: string;
            email: string;
            email_verified: boolean;
            realm_access: {
                roles: string[];
            };
        };
    };
}

function PrincipalView({principal}: { principal: Principal }) {
    return (
        <Container>
            <br/>
            <h1>You are logged in</h1>
            <br/>
            <Stack direction={"vertical"} gap={2} className="d-flex align-items-stretch">
                <Stack direction={"horizontal"} gap={2} className="d-flex align-items-stretch">
                    <Card className={"card"}>
                        <Card.Title className={"card-title"}>
                            <Stack direction={"horizontal"}>
                                <div className={"p-2"}>Name</div>
                            </Stack></Card.Title>
                        <Card.Body>{principal.userInfo.claims.name}</Card.Body>
                    </Card>
                    <Card className={"card ms-auto"}>
                        <Card.Title className={"card-title"}>
                            <Stack direction={"horizontal"} className={"d-flex align-items-stretch"}>
                                <div className={"p-2"}>Email</div>
                                <div className={"p-2 ms-auto"}>{principal.userInfo.claims.email_verified ?
                                    <Badge bg={"success"}>Verified</Badge> :
                                    <Badge bg="danger">Not verified</Badge>}
                                </div>
                            </Stack>
                        </Card.Title>
                        <Card.Body>{principal.userInfo.claims.email}</Card.Body>
                    </Card>
                    <Card className={"card ms-auto"}>
                        <Card.Title className={"card-title"}>
                            <Stack direction={"horizontal"}>
                                <div className={"p-2"}>Roles</div>
                            </Stack>
                        </Card.Title>
                        <Card.Body>
                            {principal.userInfo.claims.realm_access &&
                                principal.userInfo.claims.realm_access.roles.map((role, index) => (
                                    <Badge key={index} bg={"primary"} style={{margin: '0.25rem'}}>
                                        {role}
                                    </Badge>
                                ))}
                        </Card.Body>
                    </Card>
                </Stack>
                <Card className={"card-jwt"}>
                    <Card.Title>
                        <Stack direction={"horizontal"}>
                            <div className={"p-2"}>JWT</div>
                        </Stack></Card.Title>
                    <Card.Body>{principal.idToken.tokenValue}</Card.Body>
                </Card>
            </Stack>
        </Container>
    )
}

export default PrincipalView;