package fis_propina;

import interfaz.PropinaUI;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;

public class FIS_Propina {
    public static void main(String[] args) {

        PropinaUI p = new PropinaUI();
        p.setVisible(true);

    }

    public String calcularPropina(double color, double humedad, double temperatura, double radiacion, double concentracion) {

        // Carga el archivo de lenguaje de control difuso 'FCL'
        String fileName = "src/fis_propina/FCL_hydroponic_health.fcl";
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

        // Muestra cuanto peso tiene la variable de salida en cada CD de salida
        double pertenenciaBaja = fis.getVariable("propina").getMembership("baja");
        double pertenenciaPromedio = fis.getVariable("propina").getMembership("promedio");
        double pertenenciaGenerosa = fis.getVariable("propina").getMembership("generosa");

        String recomendacion = "";

        if (pertenenciaBaja >= pertenenciaPromedio &&
                pertenenciaBaja >= pertenenciaGenerosa){

            recomendacion = "baja";
        } else if (pertenenciaPromedio >= pertenenciaBaja &&
                pertenenciaPromedio >= pertenenciaGenerosa){
            recomendacion = "promedio";
        } else if (pertenenciaGenerosa >= pertenenciaBaja &&
                pertenenciaGenerosa >= pertenenciaPromedio){
            recomendacion = "generosa";
        }

        // Muestra las reglas activadas y el valor de salida de cada una despues de aplicar las operaciones lógicas
        StringBuilder reglasUsadas = new StringBuilder();
        reglasUsadas.append("Reglas Usadas:\n");
        fis.getFunctionBlock("prop").getFuzzyRuleBlock("No1").getRules().stream().filter(r -> (r.getDegreeOfSupport() > 0)).forEachOrdered(r -> {
            reglasUsadas.append(r.toString()).append("\n");
        });

        return ("Porcentaje de propina: " + String.format("%.1f", salida1) + "%" +
                "\n\n" + "Se recomienda dar una propina " + recomendacion +
                "\n\n" + reglasUsadas.toString());
    }
}
