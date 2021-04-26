import jfml.FuzzyInferenceSystem;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.rule.AntecedentType;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.MamdaniRuleBaseType;
import jfml.term.FuzzyTermType;

import java.util.Scanner;

public class Ejercicio3 {

    public static void ejercicio3() {
        // Input del usuario
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduzca la velocidad angular del péndulo: ");
        Float velocidadAngularEntrada = scanner.nextFloat();

        System.out.print("Introduzca el angulo del péndulo: ");
        Float anguloEntrada = scanner.nextFloat();

        String metAgr = "";
        do {
            System.out.print("Introduzca el método de agregacion (MAX o PROBOR): ");
            metAgr = scanner.next();
        } while (!metAgr.equals("MAX") && !metAgr.equals("PROBOR"));

        String metDefuzz = "";
        do {
            System.out.print("Introduzca el método de defuzzificacion (MOM o COG): ");
            metDefuzz = scanner.next();
        } while (!metDefuzz.equals("COG") && !metDefuzz.equals("MOM"));


        // Computacion del ejercicio 3
        FuzzyInferenceSystem fis = new FuzzyInferenceSystem("Péndulo invertido");
        KnowledgeBaseType kb = new KnowledgeBaseType();
        fis.setKnowledgeBase(kb);
        // Variable de entrada: ángulo
        FuzzyVariableType angulo = new FuzzyVariableType("Ángulo", -180, 180);
        angulo.setType("input");
        kb.addVariable(angulo);
        // Términos lingüísticos de Ángulo
        FuzzyTermType angNG = new FuzzyTermType("Negativo Grande",
                FuzzyTermType.TYPE_leftLinearShape, (new float[]{-30f, -15f}));
        FuzzyTermType angNP = new FuzzyTermType("Negativo Pequeño",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-30f, -15f, 0f}));
        FuzzyTermType angZ = new FuzzyTermType("Cero",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-15f, 0f, 15f}));
        FuzzyTermType angPP = new FuzzyTermType("Positivo Pequeño",
                FuzzyTermType.TYPE_triangularShape, (new float[]{0f, 15f, 30f}));
        FuzzyTermType angPG = new FuzzyTermType("Positivo Grande",
                FuzzyTermType.TYPE_rightLinearShape, (new float[]{0f, 30f}));
        angulo.addFuzzyTerm(angNG);
        angulo.addFuzzyTerm(angNP);
        angulo.addFuzzyTerm(angZ);
        angulo.addFuzzyTerm(angPP);
        angulo.addFuzzyTerm(angPG);

        // Variable de entrada velocidad angular
        FuzzyVariableType velAngular = new FuzzyVariableType("Velocidad Angular", -1, 1);
        velAngular.setType("input");
        kb.addVariable(velAngular);
        // Términos lingüísticos de velocidad angular
        FuzzyTermType velAngNG = new FuzzyTermType("Negativa Grande",
                FuzzyTermType.TYPE_leftLinearShape, (new float[]{-1f, -0.5f}));
        FuzzyTermType velAngNP = new FuzzyTermType("Negativa Pequeña",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-1f, -0.5f, 0f}));
        FuzzyTermType velAngZ = new FuzzyTermType("Cero",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-0.5f, 0f, 0.5f}));
        FuzzyTermType velAngPP = new FuzzyTermType("Positiva Pequeña",
                FuzzyTermType.TYPE_triangularShape, (new float[]{0f, 0.5f, 1f}));
        FuzzyTermType velAngPG = new FuzzyTermType("Positiva Grande",
                FuzzyTermType.TYPE_rightLinearShape, (new float[]{0.5f, 1f}));
        velAngular.addFuzzyTerm(velAngNG);
        velAngular.addFuzzyTerm(velAngNP);
        velAngular.addFuzzyTerm(velAngZ);
        velAngular.addFuzzyTerm(velAngPP);
        velAngular.addFuzzyTerm(velAngPG);

        // Variable de salida: velocidad de la plataforma
        FuzzyVariableType velocidad = new FuzzyVariableType("Velocidad", -2, 2);
        kb.addVariable(velocidad);
        velocidad.setType("output");
        velocidad.setDefaultValue(0f); // de tipo float
        velocidad.setAccumulation(metAgr);
        velocidad.setDefuzzifierName(metDefuzz);
        // Términos lingüísticos de Ángulo
        FuzzyTermType velPlaNG = new FuzzyTermType("Negativa Grande",
                FuzzyTermType.TYPE_leftLinearShape, (new float[]{-2f, -1f}));
        FuzzyTermType velPlaNP = new FuzzyTermType("Negativa Pequeña",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-2f, -1f, 0f}));
        FuzzyTermType velPlaZ = new FuzzyTermType("Cero",
                FuzzyTermType.TYPE_triangularShape, (new float[]{-1f, 0f, 1f}));
        FuzzyTermType velPlaPP = new FuzzyTermType("Positiva Pequeña",
                FuzzyTermType.TYPE_triangularShape, (new float[]{0f, 1f, 2f}));
        FuzzyTermType velPlaPG = new FuzzyTermType("Positiva Grande",
                FuzzyTermType.TYPE_rightLinearShape, (new float[]{1f, 2f}));
        velocidad.addFuzzyTerm(velPlaNG);
        velocidad.addFuzzyTerm(velPlaNP);
        velocidad.addFuzzyTerm(velPlaZ);
        velocidad.addFuzzyTerm(velPlaPP);
        velocidad.addFuzzyTerm(velPlaPG);


        // Reglas (Va, ang) -> Vp
        MamdaniRuleBaseType rb = new MamdaniRuleBaseType("Reglas");
        fis.addRuleBase(rb);

        // Regla 1  (Z, NG) -> NG
        FuzzyRuleType r1 = new FuzzyRuleType("Regla 1", "and", "MIN", 1.0f);
        AntecedentType ant1 = new AntecedentType();
        ant1.addClause(new ClauseType(velAngular, velAngZ));
        ant1.addClause(new ClauseType(angulo, angNG));
        ConsequentType con1 = new ConsequentType();
        con1.addThenClause(velocidad, velPlaNG);
        r1.setAntecedent(ant1);
        r1.setConsequent(con1);
        rb.addRule(r1);

        // Regla 2 (Z, NP) -> NP
        FuzzyRuleType r2 = new FuzzyRuleType("Regla 2", "and", "MIN", 1.0f);
        AntecedentType ant2 = new AntecedentType();
        ant2.addClause(new ClauseType(velAngular, velAngZ));
        ant2.addClause(new ClauseType(angulo, angNP));
        ConsequentType con2 = new ConsequentType();
        con2.addThenClause(velocidad, velPlaNP);
        r2.setAntecedent(ant2);
        r2.setConsequent(con2);
        rb.addRule(r2);

        // Regla 3 (Z,Z) -> Z
        FuzzyRuleType r3 = new FuzzyRuleType("Regla 3", "and", "MIN", 1.0f);
        AntecedentType ant3 = new AntecedentType();
        ant3.addClause(new ClauseType(velAngular, velAngZ));
        ant3.addClause(new ClauseType(angulo, angZ));
        ConsequentType con3 = new ConsequentType();
        con3.addThenClause(velocidad, velPlaZ);
        r3.setAntecedent(ant3);
        r3.setConsequent(con3);
        rb.addRule(r3);

        // Regla 4 (Z,PP) -> PP
        FuzzyRuleType r4 = new FuzzyRuleType("Regla 4", "and", "MIN", 1.0f);
        AntecedentType ant4 = new AntecedentType();
        ant4.addClause(new ClauseType(velAngular, velAngZ));
        ant4.addClause(new ClauseType(angulo, angPP));
        ConsequentType con4 = new ConsequentType();
        con4.addThenClause(velocidad, velPlaPP);
        r4.setAntecedent(ant4);
        r4.setConsequent(con4);
        rb.addRule(r4);

        // Regla 5 (Z,PG) -> PG
        FuzzyRuleType r5 = new FuzzyRuleType("Regla 5", "and", "MIN", 1.0f);
        AntecedentType ant5 = new AntecedentType();
        ant5.addClause(new ClauseType(velAngular, velAngZ));
        ant5.addClause(new ClauseType(angulo, angPG));
        ConsequentType con5 = new ConsequentType();
        con5.addThenClause(velocidad, velPlaPG);
        r5.setAntecedent(ant5);
        r5.setConsequent(con5);
        rb.addRule(r5);

        // Regla 6 (NG,Z) -> NG
        FuzzyRuleType r6 = new FuzzyRuleType("Regla 6", "and", "MIN", 1.0f);
        AntecedentType ant6 = new AntecedentType();
        ant6.addClause(new ClauseType(velAngular, velAngNG));
        ant6.addClause(new ClauseType(angulo, angZ));
        ConsequentType con6 = new ConsequentType();
        con6.addThenClause(velocidad, velPlaNG);
        r6.setAntecedent(ant6);
        r6.setConsequent(con6);
        rb.addRule(r6);

        // Regla 7 (NP,Z) -> NP
        FuzzyRuleType r7 = new FuzzyRuleType("Regla 7", "and", "MIN", 1.0f);
        AntecedentType ant7 = new AntecedentType();
        ant7.addClause(new ClauseType(velAngular, velAngNP));
        ant7.addClause(new ClauseType(angulo, angZ));
        ConsequentType con7 = new ConsequentType();
        con7.addThenClause(velocidad, velPlaNP);
        r7.setAntecedent(ant7);
        r7.setConsequent(con7);
        rb.addRule(r7);

        // Regla 8 (NP,PP) -> Z
        FuzzyRuleType r8 = new FuzzyRuleType("Regla 8", "and", "MIN", 1.0f);
        AntecedentType ant8 = new AntecedentType();
        ant8.addClause(new ClauseType(velAngular, velAngNP));
        ant8.addClause(new ClauseType(angulo, angPP));
        ConsequentType con8 = new ConsequentType();
        con8.addThenClause(velocidad, velPlaZ);
        r8.setAntecedent(ant8);
        r8.setConsequent(con8);
        rb.addRule(r8);

        // Regla 9 (PP,NP) -> Z
        FuzzyRuleType r9 = new FuzzyRuleType("Regla 9", "and", "MIN", 1.0f);
        AntecedentType ant9 = new AntecedentType();
        ant9.addClause(new ClauseType(velAngular, velAngPP));
        ant9.addClause(new ClauseType(angulo, angNP));
        ConsequentType con9 = new ConsequentType();
        con9.addThenClause(velocidad, velPlaZ);
        r9.setAntecedent(ant9);
        r9.setConsequent(con9);
        rb.addRule(r9);

        // Regla 10 (PP,Z) -> PP
        FuzzyRuleType r10 = new FuzzyRuleType("Regla 10", "and", "MIN", 1.0f);
        AntecedentType ant10 = new AntecedentType();
        ant10.addClause(new ClauseType(velAngular, velAngPP));
        ant10.addClause(new ClauseType(angulo, angZ));
        ConsequentType con10 = new ConsequentType();
        con10.addThenClause(velocidad, velPlaPP);
        r10.setAntecedent(ant10);
        r10.setConsequent(con10);
        rb.addRule(r10);

        // Regla 11 (PG,Z) -> PG
        FuzzyRuleType r11 = new FuzzyRuleType("Regla 11", "and", "MIN", 1.0f);
        AntecedentType ant11 = new AntecedentType();
        ant11.addClause(new ClauseType(velAngular, velAngPG));
        ant11.addClause(new ClauseType(angulo, angZ));
        ConsequentType con11 = new ConsequentType();
        con11.addThenClause(velocidad, velPlaPG);
        r11.setAntecedent(ant11);
        r11.setConsequent(con11);
        rb.addRule(r11);


        // Inferencia
        velAngular.setValue(velocidadAngularEntrada);
        angulo.setValue(anguloEntrada);
        fis.evaluate();
        System.out.println(velocidad.getValue());
    }
}
