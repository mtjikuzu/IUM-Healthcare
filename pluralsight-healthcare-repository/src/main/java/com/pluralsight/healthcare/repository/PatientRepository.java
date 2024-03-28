package com.pluralsight.healthcare.repository;

import com.pluralsight.healthcare.domain.Patient;

import java.util.List;

public interface PatientRepository {

    static PatientRepository openPatientRepository(String databaseFile) {
        return new PatientJdbcRepository(databaseFile);
    }

    void savePatient(Patient patient);

    void addNotes(String patientId, String notes);

    List<Patient> getAllPatients();

    Patient getPatientById(String patientId);
}
