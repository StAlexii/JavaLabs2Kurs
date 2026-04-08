package console;

import core.DataProcessor;
import core.FileUtils;
import models.InputArgs;

public class ConsoleApp {
    public static void main(String[] args) {
        InputArgs inputArgs = new InputArgs(args);

        if (inputArgs.getInputFile() == null) {
            System.out.println("Usage: java -jar app.jar -i input.txt [-o output.txt] [-s shift]");
            return;
        }

        try {
            String[] data = FileUtils.readLines(inputArgs.getInputFile());
            String[] result = DataProcessor.processPipeline(data, inputArgs.getShift());

            if (inputArgs.getOutputFile() != null) {
                FileUtils.writeLines(inputArgs.getOutputFile(), result);
                System.out.println("Result saved to " + inputArgs.getOutputFile());
            } else {
                for (String s : result) System.out.println(s);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}