package com.pluralsight.healthcare.server;

import com.pluralsight.healthcare.domain.Patient;
import com.pluralsight.healthcare.repository.PatientRepository;
import com.pluralsight.healthcare.repository.RepositoryException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Stream;

@Path("/patients")
public class PatientResource {
    private static final Logger LOG = LoggerFactory.getLogger(PatientResource.class);
    private static final String SECRET_KEY = "eW91ci1iYXNlNjQtZW5jb2RlZC1zZWNyZXQta2V5LWhlcmU="; // This should be managed securely

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
        // Create a sanitizer policy
        PolicyFactory sanitizer = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

        // Apply the sanitizer to the notes
        String sanitizedNotes = sanitizer.sanitize(notes);

        // Proceed with adding sanitized notes
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

    @POST
    @Path("/create")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createSession() {
        String jwt = generateJWT(); // Generate a JWT
        String cookieValue = "Authorization=" + jwt + "; Path=/; HttpOnly; Secure; SameSite=Strict";
        return Response.ok("Session created with JWT").header(HttpHeaders.SET_COOKIE, cookieValue).build();
    }

    private String generateJWT() {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 3600000; // Token is valid for 1 hour
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

        return Jwts.builder()
                .subject("subject")
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expMillis))
                .signWith(key)
                .compact();
    }
}