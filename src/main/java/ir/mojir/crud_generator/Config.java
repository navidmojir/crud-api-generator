package ir.mojir.crud_generator;

public class Config {
	private String outputDir = "./output/";
	private String entityName = "Ticket";
    private String entityNameCamel = "ticket";
    private String entityPersianName = "تیکت";
    private String packageName = "com.example.demo";
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEntityNameCamel() {
		return entityNameCamel;
	}
	public void setEntityNameCamel(String entityNameCamel) {
		this.entityNameCamel = entityNameCamel;
	}
	public String getEntityPersianName() {
		return entityPersianName;
	}
	public void setEntityPersianName(String entityPersianName) {
		this.entityPersianName = entityPersianName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
    
    
}
