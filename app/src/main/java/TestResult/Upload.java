package TestResult;

import java.io.Serializable;

public class Upload implements Serializable {
    private String type;
    private String fileUrl;
    private String note,checkedDate;
    private String patientID;
    private String documentType;

    public Upload() {
    }

    public Upload(String type, String fileUrl, String note, String checkedDate, String patientID, String documentType) {
        this.type = type;
        this.fileUrl = fileUrl;
        this.note = note;
        this.checkedDate = checkedDate;
        this.patientID = patientID;
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(String checkedDate) {
        this.checkedDate = checkedDate;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
}
