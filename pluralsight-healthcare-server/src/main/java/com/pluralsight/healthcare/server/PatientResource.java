package com.pluralsight.healthcare.server;

import com.pluralsight.healthcare.domain.Patient;
import com.pluralsight.healthcare.repository.PatientRepository;
import com.pluralsight.healthcare.repository.RepositoryException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Stream;

@Path("/patients")
public class PatientResource {
    private static final Logger LOG = LoggerFactory.getLogger(PatientResource.class);

    private final PatientRepository patientRepository;

    public PatientResource(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Stream<Patient> getPatients() {
        try {
            return patientRepository
                    .getAllPatients()
                    .stream()
                    .sorted(Comparator.comparing(Patient::id));
        } catch (RepositoryException e) {
            LOG.error("Could not retrieve patients from the database.", e);
            throw new NotFoundException();
        }
    }

    @GET
    @Path("/{patientId}/notes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientNotes(@PathParam("patientId") String patientId) {
        try {
            Patient patient = patientRepository
                    .getPatientById(patientId);

            if (patient == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Patient not found with ID: " + patientId)
                        .build();
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(patient)
                    .build();

        } catch (RepositoryException e) {
            LOG.error("Could not retrieve patient from the database.", e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while retrieving the patient.")
                    .build();
        }
    }

    @POST
    @Path("/{id}/notes")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response addNotes(@PathParam("id") String id, String notes) {
        // Encode the notes to ASCII, ignoring any non-ASCII characters, and then decode back to a string
        String sanitizedNotes = notes.chars()
                .filter(c -> c <= 127) // Restrict to ASCII characters
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        // Proceed with adding sanitized notes if validation passes
        patientRepository.addNotes(id, sanitizedNotes);

        // Return a 200 OK response indicating success
        return Response.ok().build();
    }

    @GET
    @Path("/{patientId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientById(@PathParam("patientId") String patientId) {
        try {
            Patient patient = patientRepository.getPatientById(patientId);

            if (patient == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Patient not found with ID: " + patientId)
                        .build();
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(patient)
                    .build();

        } catch (RepositoryException e) {
            LOG.error("Could not retrieve patient from the database.", e);
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while retrieving the patient.")
                    .build();
        }
    }
}