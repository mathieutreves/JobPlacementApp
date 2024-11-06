import {Button, Form, Alert} from 'react-bootstrap';
import {useState} from 'react';

class Contact {
    name: string
    surname: string

    constructor(name: string, surname: string) {
        this.name = name
        this.surname = surname
    }
}

function ContactForm({addData}: { addData(c: Contact): void }) {
    const [formData, setFormData] = useState({name: '', surname: ''});
    const [errorMsg, setErrorMsg] = useState('');

    const handleChange = (e: { target: { name: string; value: string; }; }) => {
        const {name, value} = e.target;
        setFormData(prevData => ({...prevData, [name]: value}));
    };

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();
        const {name, surname} = formData;

        if (!name) {
            setErrorMsg('Name is required');
        } else if (!surname) {
            setErrorMsg('Surname is required');
        } else {
            addData({name, surname});
            setErrorMsg('');
        }
    };

    return (
        <>
            {errorMsg && <Alert variant='danger' onClose={() => setErrorMsg('')} dismissible>{errorMsg}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className='mb-3'>
                    <Form.Label>Name</Form.Label>
                    <Form.Control
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                    />
                </Form.Group>

                <Form.Group className='mb-3'>
                    <Form.Label>Surname</Form.Label>
                    <Form.Control
                        type="text"
                        name="surname"
                        value={formData.surname}
                        onChange={handleChange}
                    />
                </Form.Group>

                <Button type='submit' variant="primary">Add</Button>
            </Form>
        </>
    );
}

export {Contact}
export default ContactForm;
