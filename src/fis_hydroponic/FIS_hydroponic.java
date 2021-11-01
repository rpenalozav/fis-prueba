package fis_hydroponic;

import interfaz.HydroponicUI;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class FIS_hydroponic {
    public static void main(String[] args) {

        HydroponicUI p = new HydroponicUI();
        p.setVisible(true);

    }

    public String calcularSalud(double color, double humedad, double temperatura, double radiacion, double concentracion) {

        // Carga el archivo de lenguaje de control difuso 'FCL'
        String fileName = "src/fis_hydroponic/FCL_hydroponic_health.fcl";
        FIS fis = FIS.load(fileName, true);

        // En caso de error
        if (fis == null) {
            System.err.println("No se puede cargar el archivo: '" + fileName + "'");
            return "";
        }

        // Establecer las entradas del sistema
        fis.setVariable("color", color);
        fis.setVariable("humedad", humedad);
        fis.setVariable("temperatura", temperatura);
        fis.setVariable("radiacion", radiacion);
        fis.setVariable("concentracion", concentracion);

        // Inicia el funcionamiento del sistema
        fis.evaluate();

        // Muestra los gráficos de las variables de entrada y salida
        JFuzzyChart.get().chart(fis.getFunctionBlock("prop"));

        /*
        // Muestra el conjunto difuso sobre el que se calcula el COG
        Variable tip = fis.getVariable("propina");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
        */

        // Imprime el valor concreto de salida del sistema
        double salida1 = fis.getVariable("enfermedad").getLatestDefuzzifiedValue();
        double salida2 = fis.getVariable("calidad").getLatestDefuzzifiedValue();
        double salida3 = fis.getVariable("crecimiento").getLatestDefuzzifiedValue();

        // Muestra cuanto peso tiene la variable de salida
        double pertenencia1 = fis.getVariable("enfermedad").getMembership("nulo");
        double pertenencia2 = fis.getVariable("enfermedad").getMembership("leve");
        double pertenencia3 = fis.getVariable("enfermedad").getMembership("grave");

        String recomendacion1 = "";

        if (pertenencia1 >= pertenencia2 &&
                pertenencia1 >= pertenencia3){

            recomendacion1 = "nulo";
        } else if (pertenencia2 >= pertenencia1 &&
                pertenencia2 >= pertenencia3){
            recomendacion1 = "leve";
        } else if (pertenencia3 >= pertenencia1 &&
                pertenencia3 >= pertenencia2){
            recomendacion1 = "grave";
        }

        // Muestra cuanto peso tiene la variable de salida en cada CD de salida
        pertenencia1 = fis.getVariable("calidad").getMembership("mala");
        pertenencia2 = fis.getVariable("calidad").getMembership("buena");
        String recomendacion2 = "";

        if (pertenencia1 >= pertenencia2){

            recomendacion2 = "mala";
        } else if (pertenencia2 >= pertenencia1){
            recomendacion2 = "buena";
        }

        // Muestra cuanto peso tiene la variable de salida en cada CD de salida
        pertenencia1 = fis.getVariable("crecimiento").getMembership("lenta");
        pertenencia2 = fis.getVariable("crecimiento").getMembership("rapida");

        String recomendacion3 = "";

        if (pertenencia1 >= pertenencia2){

            recomendacion3 = "lenta";
        } else if (pertenencia2 >= pertenencia1){
            recomendacion3 = "rapida";
        }

        // Muestra las reglas activadas y el valor de salida de cada una despues de aplicar las operaciones lógicas
        StringBuilder reglasUsadas = new StringBuilder();
        reglasUsadas.append("Reglas Usadas:\n");
        fis.getFunctionBlock("prop").getFuzzyRuleBlock("No1").getRules().stream().filter(r -> (r.getDegreeOfSupport() > 0)).forEachOrdered(r -> {
            reglasUsadas.append(r.toString()).append("\n");
        });

        String respuesta1 = "Porcentaje de enfermedad: " + String.format("%.1f", salida1) + "%" +
                "\n\n" + "Se encuentra en un estado " + recomendacion1 +
                "\n\n" + reglasUsadas.toString() + "\n\n";
        String respuesta2 = "Porcentaje de calidad: " + String.format("%.1f", salida2) + "%" +
                "\n\n" + "Se encuentra en un estado " + recomendacion2 +
                "\n\n" + reglasUsadas.toString() + "\n\n";
        String respuesta3 = "Porcentaje de crecimiento: " + String.format("%.1f", salida3) + "%" +
                "\n\n" + "Se encuentra en un estado " + recomendacion3 +
                "\n\n" + reglasUsadas.toString() + "\n\n";

        return (respuesta1+respuesta2+respuesta3);
    }
}
