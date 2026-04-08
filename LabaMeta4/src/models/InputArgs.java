package models;

public class InputArgs {
    private String inputFile;
    private String outputFile;
    private int shift = 0;
// -i <файл> — путь к входному файлу (inputFile). -o <файл> — путь к выходному файлу (outputFile). -s <число> — числовой сдвиг (shift), по умолчанию 0.
    public InputArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("-i".equals(args[i]) && i + 1 < args.length) {
                this.inputFile = args[++i];
            } else if ("-o".equals(args[i]) && i + 1 < args.length) {
                this.outputFile = args[++i];
            } else if ("-s".equals(args[i]) && i + 1 < args.length) {
                this.shift = Integer.parseInt(args[++i]);
            }
        }
    }

    public String getInputFile() { return inputFile; }
    public String getOutputFile() { return outputFile; }
    public int getShift() { return shift; }
}