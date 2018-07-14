import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;

import java.util.HashMap;

/**
 * Tutorial used to develop the class:
 * https://www.tutorialspoint.com/jfreechart/jfreechart_pie_chart.htm
 *
 * Class that uses swing and JFreeChart to produce a pie chart from provided data points.
 * Used to display election results.
 */
public class PieChart_AWT extends ApplicationFrame {

    /**
     * Constructor for PieChart_AWT
     * @param title
     * @param data
     */
    public PieChart_AWT( String title, HashMap<String, Integer> data) {
        //Calls constructor of ApplicationFrame to set title of frame.
        super( title );

        //Sets the content pane with the created demo panel
        setContentPane(createDemoPanel(data));
    }

    /**
     * Method to create data set of pie chart from results hash map.
     * @param data - hashmap of election results.
     * @return the completed pie chart data set.
     */
    public PieDataset createDataset(HashMap<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset( );

        //Iterates over hash map and if the values are not null sets the current key and value as a value on the
        //pie chart data set.
        for (String key: data.keySet()) {
            if (key != null && data.get(key) != null) {
                dataset.setValue(key, new Double(data.get(key)));
            }
        }

        return dataset;
    }


    /**
     * Method to create a pie chart using a pie chart data set.
     * @param dataSet derived from results of election.
     * @return the chart
     */
    public JFreeChart createChart( PieDataset dataSet ) {
        JFreeChart chart = ChartFactory.createPieChart(
                "Election",   // chart title
                dataSet,          // data
                true,             // include legend
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLegendLabelGenerator(
                new StandardPieSectionLabelGenerator("{0} {1} {2}"));

        return chart;
    }

    /**
     * Creates a demo panel using a chart created from a data set of election results.
     * @param data to be used in chart.
     * @return chart panel to be displayed.
     */
    public JPanel createDemoPanel(HashMap<String, Integer> data) {
        JFreeChart chart = createChart(createDataset(data) );
        return new ChartPanel( chart );
    }
}