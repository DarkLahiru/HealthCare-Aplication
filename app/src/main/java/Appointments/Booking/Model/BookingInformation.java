package Appointments.Booking.Model;

public class BookingInformation {
    private String status,patientName,patientPhone,patientID,time,doctorID,doctorName,locationId,locationName,locationAddress,docPhone,date,nodeKey;
    private Long slot;

    public BookingInformation() {
    }

    public BookingInformation(String status, String patientName, String patientPhone, String patientID, String time, String doctorID, String doctorName, String locationId, String locationName, String locationAddress, String docPhone, String date, String nodeKey, Long slot) {
        this.status = status;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.patientID = patientID;
        this.time = time;
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.docPhone = docPhone;
        this.date = date;
        this.nodeKey = nodeKey;
        this.slot = slot;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getDocPhone() {
        return docPhone;
    }

    public void setDocPhone(String docPhone) {
        this.docPhone = docPhone;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }
}
