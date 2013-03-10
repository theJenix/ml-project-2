import java.io.IOException;

import opt.EvaluationFunction;
import opt.OptimizationAlgorithm;
import shared.FixedIterationTrainer;
import shared.writer.CSVWriter;
import shared.writer.Writer;

public class BaseTest {

    /**
     * @param optimizationAlgorithmFactory
     * @param ef
     * @param trainItr
     * @param runItr
     * @return
     * @throws IOException
     */
    protected void runOne(OptimizationAlgorithmFactory factory, EvaluationFunction ef,
            int trainItr, int runItr) throws IOException {
        FixedIterationTrainer fit;
        OptimizationAlgorithm oa = factory.newOptimizationAlgorithm();
        String fileName = this.getClass().getName() + "_"
                + getCapitals(oa.getClass().getName()) + ".csv";
        Writer csv = new CSVWriter(fileName, new String[] { "iteration",
                "value" });
        csv.open();
        long time = System.currentTimeMillis();
        int ii = 1;
        try {
            do {
                csv.write("" + ii);
                fit = new FixedIterationTrainer(oa, trainItr);
                fit.train();
                csv.write("" + ef.value(oa.getOptimal()));
                csv.nextRecord();
                oa = factory.newOptimizationAlgorithm();
            } while (++ii <= runItr);
        } finally {
            time = System.currentTimeMillis() - time;
            long min = time / 60000;
            time -= min * 60000;
            long sec = time / 1000;
            System.out.println(String.format("Elapsed time: %02d:%02d", min, sec));
            csv.close();
        }
    }

    private String getCapitals(String name) {
        StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < name.length(); ii++) {
            char chr = name.charAt(ii);
            if ((chr & 0x20) == 0) {
                builder.append(chr);
            }
        }
        return builder.toString();
    }
}
