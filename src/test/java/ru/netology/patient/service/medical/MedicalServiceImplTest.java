package ru.netology.patient.service.medical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MedicalServiceImplTest {

    private PatientInfoRepository patientInfoRepository;
    private SendAlertService sendAlertService;
    private MedicalServiceImpl medicalService;

    private static final String PATIENT_ID = "1";
    private PatientInfo patientInfo;

    @BeforeEach
    void setUp() {
        patientInfoRepository = mock(PatientInfoRepository.class);
        sendAlertService = mock(SendAlertService.class);
        medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);

        BloodPressure normalPressure = new BloodPressure(120, 80);
        BigDecimal normalTemperature = new BigDecimal("36.6");

        patientInfo = new PatientInfo(
                PATIENT_ID, "Иван", "Иванов", LocalDate.of(1990, 1, 1),
                new HealthInfo(normalTemperature, normalPressure)
        );

        when(patientInfoRepository.getById(PATIENT_ID)).thenReturn(patientInfo);
    }

    @Test
    void testCheckBloodPressure_AlertSent() {
        BloodPressure abnormalPressure = new BloodPressure(100, 70);

        medicalService.checkBloodPressure(PATIENT_ID, abnormalPressure);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sendAlertService).send(captor.capture());
        assertEquals("Warning, patient with id: 1, need help", captor.getValue());
    }

    @Test
    void testCheckTemperature_AlertSent() {
        BigDecimal highTemperature = new BigDecimal("38.2");

        medicalService.checkTemperature(PATIENT_ID, highTemperature);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sendAlertService).send(captor.capture());
        assertEquals("Warning, patient with id: 1, need help", captor.getValue());
    }

    @Test
    void testNoAlertWhenValuesAreNormal() {
        BloodPressure normalPressure = new BloodPressure(120, 80);
        BigDecimal normalTemperature = new BigDecimal("36.6");

        medicalService.checkBloodPressure(PATIENT_ID, normalPressure);
        medicalService.checkTemperature(PATIENT_ID, normalTemperature);

        verify(sendAlertService, never()).send(any());
    }
}
