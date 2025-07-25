package entities;

public class Scan {
    private Long id;
    private String fileName;
    private String fileType;
    private byte[] image;
    private Cheque cheque;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    public Cheque getCheque() { return cheque; }
    public void setCheque(Cheque cheque) { this.cheque = cheque; }
}