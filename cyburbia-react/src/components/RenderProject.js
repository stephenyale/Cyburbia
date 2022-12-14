import { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';

const PROJ_DEFAULT = {
    projectId: '',
    locationId: '',
    agencyId: '',
    sqFt: '',
    projectType: '',
    status: '',
    description: '',
    budget: '',
    developers: []
}

function convertStatus(input) {
    if (input === "PRO") {
        return "Proposed"
    }
    if (input === "REV") {
        return "In Review"
    }
    if (input === "APP") {
        return "Approved"
    }
    if (input === "CON") {
        return "Under Construction"
    }
    if (input === "COM") {
        return "Completed"
    }
    if (input === "CAN") {
        return "Canceled"
    }
}

function convertType(input) {
    if (input === "RES") {
        return "Residential"
    }
    if (input === "IND") {
        return "Industrial"
    }
    if (input === "IND") {
        return "Industrial"
    }
    if (input === "COM") {
        return "Commercial"
    }
    if (input === "AGR") {
        return "Agricultural"
    }
    if (input === "REC") {
        return "Recreational"
    }
    if (input === "INS") {
        return "Institutional"
    }
    if (input === "TRA") {
        return "Transportation"
    }
    if (input === "MIX") {
        return "Mixed-Urban"
    }
    if (input === "NAT") {
        return "Natural"
    }
}

function RenderProject({ id }) {
    const [project, setProject] = useState(PROJ_DEFAULT);

    // const { id } = useParams();

    useEffect(() => {
        // Make sure that we have an "id" value...
        if (id) {
            fetch(`http://localhost:8080/api/project/${id}`)
                .then(response => {
                    if (response.status === 200) {
                        return response.json();
                    } else {
                        return Promise.reject(`Unexpected status code: ${response.status}`);
                    }
                })
                .then(data => setProject(data))
                .catch(console.log);
        }
    }, [id]);



    return (
        <>
            <div className="container">
                <h3 className="mb-2 mt-2">{convertStatus(project.status)} - {convertType(project.projectType)}</h3>
                <h5 className="mb-2 mt-4">{project.description}</h5>
                <br></br>
                <h4 className="mb-2 mt-2">Budget: </h4>${project.budget.toLocaleString('en-US')}
                <h4 className="mb-2 mt-2">Sq. Footage: </h4>{project.sqFt.toLocaleString('en-US')}
            </div>
        </>
    )
}

export default RenderProject;