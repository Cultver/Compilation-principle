import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author WH
 * @date 2022.4.22
 * @motto 不满足是向上的车轮!
 */
public class AnalysisDemo extends JFrame {
    private identifierTable identifiertable = new identifierTable();
    private MiddleCodeTable codeTable = new MiddleCodeTable();
    private tempVarTable tempVarTable = new tempVarTable();
    private static String code = "";
    private StringBuilder inputProgram;//resourse code
    private int inputPointer;//读头指针
    private String temp;//正在读取单词的字符串
    private int state;//FA state number
    private twoTuple word;
    List<String> identifyCheckList = new ArrayList<>();
    private ArrayList<String> identifierCheckList = new ArrayList<String>();

    public AnalysisDemo() {
        initComponents();
    }

    private class twoTuple {
        public String type;
        public String value;

        public twoTuple(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + this.type + ", " + this.value + ")";
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private class fourTuple {
        public String operator;
        public String operand1;
        public String operand2;
        public String result;

        public fourTuple(String operator, String operand1, String operand2, String result) {
            this.operator = operator;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.result = result;
        }

        @Override
        public String toString() {
            return "(" + operator + ", " + operand1 + ", " + operand2 + ", " + result + ')';
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getOperand1() {
            return operand1;
        }

        public void setOperand1(String operand1) {
            this.operand1 = operand1;
        }

        public String getOperand2() {
            return operand2;
        }

        public void setOperand2(String operand2) {
            this.operand2 = operand2;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    private class MiddleCodeTable {
        private ArrayList<fourTuple> table = new ArrayList<fourTuple>();

        public int NXQ() {
            return table.size();
        }

        public MiddleCodeTable() {
//            table = new ArrayList<fourTuple>();
        }

        public boolean add(String operator, String operand1, String operand2, String result) {
            this.table.add(new fourTuple(operator, operand1, operand2, result));
            return true;
        }

        public boolean Backpath(int inputPointer, String result) {
            if (inputPointer < 0 || inputPointer > (table.size() - 1)) return false;
            this.table.get(inputPointer).setResult(result);
            return true;
        }

        public void Clear() {
            table.clear();
        }

        public void dump(JTextArea ta) {
            for (int i = 0; i < table.size(); i++) {
                ta.append("(" + i + ") " + table.get(i).toString() + "\n");
            }
        }
    }

    private identifier identifyTable(String name) {
        identifier identify = new identifier(name);
        identify.setName(name);
        return identify;
    }

    private class identifier {
        public String name;
        public String type;
        public String value;

        public identifier(String name) {
            this.name = name;
            this.type = "";
            this.value = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private class identifierTable {
        private ArrayList<identifier> table;

        public identifierTable() {
            table = new ArrayList<identifier>();
        }

        public boolean exitIdentifier(String name) {
            for (identifier id : table) {
                if (id.getName().equals(name))
                    return true;
            }
            return false;
        }

        public boolean add(String name) {
            if (this.exitIdentifier(name))
                return false;
            this.table.add(new identifier(name));
            return true;
        }

        public int getinputPointerByName(String name) {
            for (int i = 0; i < table.size(); i++) {
                if (table.get(i).getName().equals(name)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean updateIdentifierTypeByName(String name, String type) {
//            if(this.exitIdentifier(name)){
//                return false;
//            }
            int inputPointer = this.getinputPointerByName(name);
            if (inputPointer == -1) {
                return false;
            }
            table.get(inputPointer).setType(type);
            return true;
        }

        public boolean updateIdentifierValueByName(String name, String value) {
//            if(this.exitIdentifier(name)){
//                return false;
//            }
            int inputPointer = this.getinputPointerByName(name);
            if (inputPointer == -1) {
                return false;
            }
            table.get(inputPointer).setValue(value);
//            System.out.println(table.get(inputPointer).value);
            return true;
        }

        public String getIdentifierValueByNameAsString(String name) {
            int inputPointer = this.getinputPointerByName(name);
            if (inputPointer == -1) {
//                return "error identifier not exist";
                return "";
            }
            return table.get(inputPointer).getValue();
        }

        public int getIdentifierValueByNameAsInt(String name) {
            int inputPointer = this.getinputPointerByName(name);
            if (inputPointer == -1) {
                return -1;
            }
            if (table.get(inputPointer).getValue().isEmpty()) {
                return -1;
            }
            return Integer.parseInt(table.get(inputPointer).getValue());
        }

        public void Clear() {
            table.clear();
        }

        public String dump() {
            String s = "";
            for (identifier id : table) {
//                System.out.println(id);
                s += id.getName() + "," + id.getType() + ", " + id.getValue() + ";";
            }
            return s;
        }
    }

    private class tempVarTable {
        private ArrayList<String> table = new ArrayList<String>();
        ;

        public tempVarTable() {
        }

        public identifier getNewTempVar() {
            String tmp = "";

            int inputPointer = table.size();
            String name = "T" + String.valueOf(inputPointer);
            table.add(name);
            return new identifier(name);
        }

        public void Clear() {
            table.clear();
        }

        public String dump() {
            String s = "";
            for (String i : table) {
                s += i + ";";
            }
            return s;
        }
    }

    private twoTuple nextInput() throws NullPointerException {
        temp = "";
        state = 1;
        char symbol;
        while (!(Character.toString(inputProgram.charAt(inputPointer)).equals("#"))) {
//            System.out.println("true");
            symbol = inputProgram.charAt(inputPointer);
            if (symbol == ' ') {
                inputPointer++;
                continue;
            }
            if (state == 1 && symbol == '$') {
                temp += symbol;
                state = 2;
                inputPointer++;
                continue;
            }
            if (state == 2 && (symbol >= 'a' && symbol <= 'z')) {
                state = 3;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 3) {
                if ((symbol >= 'a' && symbol <= 'z') || (symbol >= '1' && symbol <= '9')) {
                    state = 3;
                    temp += symbol;
                    inputPointer++;
                    continue;
                } else {
                    identifiertable.add(temp);
                    return new twoTuple("标识符", temp);
                }
            }//identifier

            if (state == 1 && symbol == ';') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("分号", temp);

            }//;

            if (state == 1 && symbol == ',') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("逗号", temp);
            }//,

            if (state == 1 && symbol == '=') {
                state = 6;
                temp += symbol;
                inputPointer++;
                continue;
            }//
            if (state == 6) {
                if (symbol == '=') {
                    temp += symbol;
                    inputPointer++;
                    return new twoTuple("关系运算符", temp);
                } else {
                    return new twoTuple("赋值号", temp);
                }
            }//=


            if (state == 1 && symbol == '<') {
                state = 8;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 8) {
                if (symbol == '=') {
                    temp += symbol;
                    inputPointer++;
                    return new twoTuple("关系运算符", temp);
                } else {
                    return new twoTuple("关系运算符", temp);
                }
            }//<=

            if (state == 1 && symbol == '>') {
                state = 9;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 9) {
                if (symbol == '=') {
                    temp += symbol;
                    inputPointer++;
                    return new twoTuple("关系运算符", temp);
                } else {
                    return new twoTuple("关系运算符", temp);
                }
            }//>=
            if (state == 1 && symbol == '(') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("左括号", temp);
            }//(
            if (state == 1 && symbol == ')') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("右括号", temp);
            }//)

            if (state == 1 && Character.toString(symbol).equals("i")) {
                state = 12;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 12 && Character.toString(symbol).equals("n")) {
                state = 13;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 13 && Character.toString(symbol).equals("t")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("int", temp);
            }//int

            if (state == 12 && Character.toString(symbol).equals("f")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("if", temp);
            }//if

            if (state == 1 && Character.toString(symbol).equals("t")) {
                state = 16;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 16 && Character.toString(symbol).equals("h")) {
                state = 17;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 17 && Character.toString(symbol).equals("e")) {
                state = 18;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 18 && Character.toString(symbol).equals("n")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("then", temp);
            }//then

            if (state == 1 && Character.toString(symbol).equals("e")) {
                state = 20;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 20 && Character.toString(symbol).equals("l")) {
                state = 21;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 21 && Character.toString(symbol).equals("s")) {
                state = 22;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 22 && Character.toString(symbol).equals("e")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("else", temp);
            }//else

            if (state == 20 && Character.toString(symbol).equals("n")) {
                state = 24;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 24 && Character.toString(symbol).equals("d")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("end", temp);
            }//end

            if (state == 1 && Character.toString(symbol).equals("b")) {
                state = 26;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 26 && Character.toString(symbol).equals("e")) {
                state = 27;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 27 && Character.toString(symbol).equals("g")) {
                state = 28;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 28 && Character.toString(symbol).equals("i")) {
                state = 29;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 29 && Character.toString(symbol).equals("n")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("begin", temp);
            }//begin

            if (state == 1 && Character.toString(symbol).equals("w")) {
                state = 31;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 31 && Character.toString(symbol).equals("h")) {
                state = 32;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 32 && Character.toString(symbol).equals("i")) {
                state = 33;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 33 && Character.toString(symbol).equals("l")) {
                state = 34;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 34 && Character.toString(symbol).equals("e")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("while", temp);
            }//while

            if (state == 1 && (symbol >= '0' && symbol <= '9')) {
                state = 36;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 36) {
                if (symbol >= '0' && symbol <= '9') {
                    state = 36;
                    temp += symbol;
//                    code += temp + " ;";
                    inputPointer++;
                    continue;
                } else {
                    code += temp + " ;";
                    return new twoTuple("数字", temp);
                }
            }//number

            if (state == 1 && symbol == '+') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("加法运算符", temp);
            }//+
            if (state == 1 && symbol == '*') {
                temp += symbol;
                inputPointer++;
                return new twoTuple("乘法运算符", temp);
            }//*

            if (state == 1 && Character.toString(symbol).equals("d")) {
                state = 39;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 39 && Character.toString(symbol).equals("o")) {
                temp += symbol;
                inputPointer++;
                return new twoTuple("do", temp);
            }//do
            if (state == 1 && symbol == '!') {
                state = 42;
                temp += symbol;
                inputPointer++;
                continue;
            }
            if (state == 42) {
                if (symbol == '=') {
                    temp += symbol;
                    inputPointer++;
                    return new twoTuple("关系运算符", temp);
                }
            }//!=
//            System.out.println(inputPointer);
            inputPointer++;
            temp += symbol;
//            return new twoTuple("#","#");
            return new twoTuple("error", temp);


        }
//        return new twoTuple("error","error");
        return new twoTuple("#", "#");
    }

    private boolean match(String type) {
        if (!type.equals(word.getType())) {
            output("匹配失败!" + word.getValue());
            word = nextInput();
            output("[词]识别单词: " + word.toString());
            return false;
        }
        output("匹配成功!");
        word = nextInput();
        output("[词]识别单词 : " + word.toString());
        return true;
    }

    private boolean parseProgram() {
        output("推导:<程序> →<变量说明部分>;<语句部分>");

        boolean T = parseExplainVars();
        if (!T) {
            output("语法错误：缺少变量申明");
        }
        match("分号");
        parseStatementSection();

        output("语法分析结束");
        return true;
    }

    private boolean parseExplainVars() {
        output("推导:<变量说明部分> → int<标识符列表> ");
//        boolean T = match("int");
//        if (T) {
//            parseIdentifierList("int");
//            return true;
//        } else {
//            output("语义错误：变量说明部分错误");
//            return false;
//        }
        boolean T = match("int");
        if (!T)
            return false;
        else {
            parseIdentifierList("int");
            return true;
        }

    }

    private boolean parseIdentifierList(String type) {
        output("推导:<标识符列表> → <标识符><标识符列表prime>");
        if (word.getType().equals("标识符")) {
            identifiertable.updateIdentifierTypeByName(word.getValue(), "int");
            output("更新" + word.getValue() + "类型为int");
//            identifierCheckList.add(word.value);//加入标识符重复检查列表
            identifyCheckList.add(word.getValue());
            match("标识符");
//            parseIdentifierListPrime(type);
        } else {
            output("语法错误, 缺少标识符");
        }
        parseIdentifierListPrime(type);
        return true;
    }

    private boolean parseIdentifierListPrime(String type) {
        output("推导:<标识符列表prime> → ,<标识符><标识符列表prime>|ε");
        if (word.getType().equals("逗号")) {
            match("逗号");
            if (word.getType().equals("标识符")) {
                boolean isCheck = false;
                for (String i : identifyCheckList) {
                    if (word.getValue().equals(i)) {
                        output("语义错误: 重复定义");
                        isCheck = true;
                    }
                }
                if (!isCheck)
                    identifyCheckList.add(word.getValue());
                identifiertable.updateIdentifierTypeByName(word.getType(), "int");
                output("更新标识符" + word.getValue() + "类型为int");
                match("标识符");
            } else {
                output("语法错误, 缺少标识符");
//                output("语义错误, 标识符列表prime翻译失败");
            }
            parseIdentifierListPrime(type);
        } else {

        }

        return true;
    }

//    private boolean parseIdentifier(){
//        output("推导:<标识符> → $<字母><标识符prime>");
//        match("标识符");
//        parseLetter();
//        parseIdentifierPrime();
//        return true;
//    }
//    private boolean parseIdentifierPrime(){
//        output("推导:<标识符prime> → $(<字母>|<数字>)<标识符prime>|ε");
//        if(word.type=="标识符"){
//            match("标识符");
//            match("左括号");
//            if(word.type=="标识符"){
//                parseLetter();
//            }
//            if(word.type=="数字"){
//                parseNumber();
//            }
//            match("右括号");
//            parseIdentifierPrime();
//        }else{
//
//        }
//        return true;
//    }

    private boolean parseStatementSection() {
        output("推导:<语句部分> → <语句>;<语句部分prime>");
        parseStatement();
        match("分号");
        parseStatementSectionPrime();
        return true;
    }

    private boolean parseStatementSectionPrime() {
        output("推导:<语句部分prime> → <语句>;<语句部分prime>|ε");
        if (word.getType().equals("标识符") || word.getType().equals("if") || word.getType().equals("while")) {
            parseStatement();
            match("分号");
            parseStatementSectionPrime();
        } else {
//            System.out.println("词法分析结束");
        }
        return true;
    }

    private boolean parseStatement() {
//        output("推导:<语句> → <赋值语句>|<条件语句>|<循环语句>");
        if (word.getType().equals("标识符")) {
            output("推导:<语句> → <赋值语句>");
            parseAssignStatement();
        }
        if (word.getType().equals("if")) {
            output("推导:<语句> → <条件语句>");
            parseIfStatement();
        }
        if (word.getType().equals("while")) {
            output("推导:<语句> →<循环语句>");
            parseCircleStatement();
        }
        return true;
    }

    private boolean parseAssignStatement() {
        output("推导:<赋值语句> → <标识符>=<表达式>");
        String identifiername = "";
        if (word.getType().equals("标识符")) {
            identifiername = word.getValue();
//            System.out.println(identifiername);
            output("[翻]获取赋值语句标识符" + word.getValue());
            match("标识符");
        }
        boolean len = match("赋值号");
//        if (!len) {
//            output("语法错误: 语句错误");
//        }
        identifier E = parseExpression();
        output("[翻]输出赋值语句四元式");
        codeTable.add("=", E.getName(), "null", identifiername);
        identifiertable.updateIdentifierValueByName(identifiername, E.getValue());
        output("[翻]更新标识符" + identifiername + "值为" + E.getValue());
//        System.out.println(E.value);
        return true;
    }

    private boolean parseIfStatement() {
        output("推导:<条件语句> → if （<条件>） then <嵌套语句>; else <嵌套语句>");
        match("if");
        match("左括号");
        identifier E = parseLogicExpression();
        match("右括号");
        match("then");
        output("[翻]输出if语句真出口跳转四元式");
//        codeTable.add("jnz", E.getName(), "null", (Integer.toString(codeTable.NXQ() + 2)));
        codeTable.add("jnz", E.getName(), "null", (String.valueOf(codeTable.NXQ() + 2)));
        int falseExitinputPointer = codeTable.NXQ();
        output("[翻]输出if语句假出口跳转四元式");
        codeTable.add("j", "null", "null", "0");//<-False
        int exitinputPointer = 0;
        if (word.getType().equals("begin")) {
            output("推导: <嵌套语句> → <复合语句>");
            parseCompoundStatement();
            exitinputPointer = codeTable.NXQ();
            codeTable.add("j", "null", "null", "0");//<-Exit
            output("[翻]回填if语句假出口地址");
            codeTable.Backpath(falseExitinputPointer, String.valueOf(codeTable.NXQ()));
        } else {
            output("推导: <嵌套语句> → <语句>");
            parseStatement();
            exitinputPointer = codeTable.NXQ();
            codeTable.add("j", "null", "null", "0");//<-Exit
            output("[翻]回填if语句假出口地址");
            codeTable.Backpath(falseExitinputPointer, String.valueOf(codeTable.NXQ()));
        }
        match("分号");
        match("else");
        if (word.getType().equals("begin")) {
            output("推导: <嵌套语句> → <复合语句>");
            parseCompoundStatement();
            output("[翻]回填if语句假出口地址");
            codeTable.Backpath(exitinputPointer, Integer.toString(codeTable.NXQ()));
        } else {
            output("推导: <嵌套语句> → <语句>");
            parseStatement();
            output("[翻]回填if语句假出口地址");
            codeTable.Backpath(exitinputPointer, Integer.toString(codeTable.NXQ()));
        }
        return true;
    }

    private boolean parseCircleStatement() {
        output("推导:<循环语句> → while （<条件>） do <嵌套语句>");
        match("while");
        match("左括号");
        int number = codeTable.NXQ();
        identifier E = parseLogicExpression();
        match("右括号");
        match("do");
        output("[翻]输出while语句真出口跳转四元式");
        codeTable.add("jnz", E.getName(), "null", (Integer.toString(codeTable.NXQ() + 2)));
        int falseExitinputPointer = codeTable.NXQ();
        output("[翻]输出while语句真出口跳转四元式");
        codeTable.add("j", "null", "null", "0");//<-False
        int exitinputPointer = 0;
        if (word.getType().equals("begin")) {
            output("推导: <嵌套语句> → <复合语句>");
            parseCompoundStatement();
            exitinputPointer = codeTable.NXQ();
            codeTable.add("j", "null", "null", "0");//<-Exit
            codeTable.Backpath(falseExitinputPointer, String.valueOf(codeTable.NXQ()));
        } else {
            output("推导: <嵌套语句> → <语句>");
            parseStatement();
            exitinputPointer = codeTable.NXQ();
            codeTable.add("j", "null", "null", "0");//<-Exit
//            output("[翻]回填if语句假出口地址");
            codeTable.Backpath(falseExitinputPointer, String.valueOf(codeTable.NXQ()));
            output("[翻]回填while语句出口地址");
            codeTable.Backpath(exitinputPointer, String.valueOf(number));
        }

        return true;
    }

    private identifier parseExpression() {
        output("推导:<表达式> → <项><表达式prime>");
        identifier E1 = parseItem();
        identifier E2 = parseExpressionPrime(E1);
        return E2;
    }

    private identifier parseExpressionPrime(identifier E) {
        if (word.getType().equals("加法运算符")) {
            output("推导:<表达式prime> → +<项><表达式prime>|ε");
            match("加法运算符");
            identifier E2 = parseItem();
            output("[翻]创建加法运算临时变量");
            identifier T = tempVarTable.getNewTempVar();

            codeTable.add("+", E.getName(), E2.getName(), T.getName());
            output("[翻]输出加法运算四元式");

            if (E.getValue().isEmpty()) {
                output("语义错误：" + E.getName() + "未赋值");
            }
            if (!E.getValue().isEmpty() && !E2.getValue().isEmpty()) {
//                System.out.println(E.getValue());
//                System.out.println(E2.value);
                T.setValue(String.valueOf(Integer.parseInt(E.getValue()) + Integer.parseInt(E2.getValue())));
            } else {
                output("语义错误：项计算失败");
                output("语义错误：表达式计算失败");
            }
            identifier E3 = parseExpressionPrime(T);
            return E3;
        } else {
            return E;
        }
    }

    private identifier parseItem() {
        output("推导:<项> → <因子><项prime>");
        identifier E = parseFactor();
        identifier E1 = parseItemPrime(E);
        if (!E1.getType().isEmpty()) {
            return E1;
        } else {
            return E;
        }
    }

    private identifier parseItemPrime(identifier E) {
        if (word.getType().equals("乘法运算符")) {
            output("推导:<项prime> → *<因子><项prime>|ε");
            match("乘法运算符");
            identifier E2 = parseFactor();
            output("[翻]创建乘法运算临时变量");
            identifier T = tempVarTable.getNewTempVar();
//            System.out.println(T);
            codeTable.add("*", E.getName(), E2.getName(), T.getName());
            output("[翻]输出乘法四元式");
//?
            if (!E.getValue().isEmpty() && !E2.getValue().isEmpty()) {
                T.setValue(String.valueOf(Integer.parseInt(E.getValue()) * Integer.parseInt(E2.getValue())));
//                System.out.println(T.value);
            } else {
                output("语义错误：因子计算失败");
            }
            identifier E3 = parseItemPrime(T);
            if (!E3.getValue().isEmpty()) {
                return E3;
            } else {
                return T;
            }
        } else if (word.getType().equals("左括号")) {
            output("推导: <项prime> → <因子><项prime>");
            match("左括号");
            identifier E2 = parseFactor();
            identifier E3 = parseItemPrime(E2);
            return E3;
        }
        return E;
    }

    private identifier parseFactor() {
        identifier E = identifyTable("");
        if (word.getType().equals("标识符")) {
            output("推导:<因子> → <标识符>");
            E = identifyTable(word.getValue());
            E.setValue(identifiertable.getIdentifierValueByNameAsString(word.getValue()));
//            System.out.println(E.value);
            match("标识符");
        } else if (word.getType().equals("数字")) {
            output("推导:<因子> → <常量>");
            E = identifyTable(word.getValue());
            E.setValue(word.getValue());
//            System.out.println(E.value);
            match("数字");
        } else if (word.getType().equals("左括号")) {
            output("推导:<因子> → (<表达式>)");
            match("左括号");
            E = parseExpression();
//            System.out.println(E.value);
            match("右括号");
        }
        return E;
    }

//    private boolean parseConst(){
//        output("推导:<常量> → <无符号整数>");
//        parseUnsignedInt();
//        return true;
//    }
//    private boolean parseUnsignedInt(){
//        output("推导:<无符号整数> → <数字序列>");
//        parseNumberList();
//        return true;
//    }
//    private boolean parseNumberList(){
//        output("推导:<数字序列> → <数字><数字序列prime>");
//        match("数字");
//        parseNumberListPrime();
//        return true;
//    }
//    private boolean parseNumberListPrime(){
//        output("推导:<数字序列prime> → <数字><数字序列prime>|ε");
//        if(word.type=="数字"){
//            match("数字");
//            parseNumberListPrime();
//        }else{
//
//        }
//        return true;
//    }
//    private boolean parseAddOperator(){
//        output("推导:<加法运算符> → +");
//        return true;
//    }
//    private boolean parseMulOperator(){
//        output("推导:<乘法运算符> → *");
//        return true;
//    }
//    private boolean parseRelOperator(){
//        output("推导:<关系运算符> → <|>|!= |>=|<= |==");
//        match("逻辑运算符");
//        return true;
//    }

    private identifier parseLogicExpression() {
        output("推导:<条件> → <表达式><关系运算符><表达式>");
        identifier E = parseExpression();
        String logicOperator = word.getValue();
//        if (word.getType().equals("关系运算符")) {
//            match("关系运算符");
//        }
        boolean temp = match("关系运算符");
        if (!temp) {
            output("语法错误: 非关系运算符");
        }
        identifier E2 = parseExpression();
        identifier T = tempVarTable.getNewTempVar();
        codeTable.add(logicOperator, E.getName(), E2.getName(), T.getName());
        output("[翻]输出逻辑运算四元式");

        T.setValue("boolean");
        int value1 = identifiertable.getIdentifierValueByNameAsInt(E.getName());
        int value2 = identifiertable.getIdentifierValueByNameAsInt(E2.getName());
        if (logicOperator.equals("<")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 < value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        if (logicOperator.equals(">")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 > value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        if (logicOperator.equals("==")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 == value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        if (logicOperator.equals("<=")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 <= value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        if (logicOperator.equals(">=")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 >= value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        if (logicOperator.equals("!=")) {
            if (value1 == -1) {
//                return null;
            }
            if (value1 != value2) {
                T.setValue("true");
            } else {
                T.setValue("false");
            }
        }
        return T;
    }

    private boolean parseCompoundStatement() {
        output("推导:<复合语句> → begin <语句部分> end");
        match("begin");
        parseStatementSection();
        match("end");
        return true;
    }

    //    private boolean parseLetter(){
//        output("推导:<字母> → a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z");
//        return true;
//    }
//    private boolean parseNumber(){
//        output("推导:<数字> → 0|1|2|3|4|5|6|7|8|9");
//        return true;
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        fileChooser = new JFileChooser();
//        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jScrollPane1 = new JScrollPane();
        textarea = new JTextArea();
        browse = new JButton();
        jTextField1 = new JTextField();
        jScrollPane2 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jScrollPane3 = new JScrollPane();
        jTextArea2 = new JTextArea();

        fileChooser.setDialogTitle("This is my open dialog");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文件(*.txt)", "txt");
        fileChooser.setFileFilter(filter);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        jButton1.setText("lexical" +
//                " only");
        jButton2.setText("lexical " +
                "syntax");
        jButton3.setText("output ");
//        jButton1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                jButton1ActionPerformed(evt);
//            }
//        });
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton2ActionPerformed(e);
            }
        });
//        jButton3.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                try {
//                    jButton3ActionPerformed(evt);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //当按钮被点击时，Swing框架会调用监听器的actionPerformed()方法
                System.out.println("按钮被点击....");
                String tmp = "";
                for (fourTuple i : codeTable.table) {
                    tmp = tmp + i.toString() + "\n";
                }
                writeToTXT(tmp);
            }
        });

        textarea.setColumns(20);
        textarea.setRows(5);
        jScrollPane1.setViewportView(textarea);

        browse.setText("浏览");
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        jTextField1.setText("请将文件拖入该窗口，或者点击右侧浏览");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(browse, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 477, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
//                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
                                                .addGap(328, 328, 328)))
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap(569, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE)
                                        .addGap(299, 299, 299)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(68, 68, 68)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(browse, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                                                .addGap(94, 94, 94)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
//                                                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 264, GroupLayout.PREFERRED_SIZE)
                                                ))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jScrollPane2)))
                                .addContainerGap())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
                                        .addContainerGap()))
        );

        pack();
    }// </editor-fold>

    private void output(String s) {
        //listBox.Items.Add(s);
        jTextArea2.append(s);
        jTextArea2.append("\n");
//        jTextArea2.setLineWrap(true);
    }

    // TODO: 点击按钮二元式
//    private void jButton1ActionPerformed(ActionEvent evt) {
//        jTextArea2.setText("");//清空左侧输出栏
//        identifiertable.Clear();
//        code = "";
//
//        String d = "#";
//        inputProgram = new StringBuilder(textarea.getText());
//        inputProgram.append(d);
//
////        output(inputProgram);
//        inputPointer = 0;
//
//        temp = "";
//        state = 1;
//        char symbol;
//        while (!Character.toString(inputProgram.charAt(inputPointer)).equals("#")) {
//            symbol = inputProgram.charAt(inputPointer);
//            if (symbol == ' ') {
//                inputPointer++;
//                continue;
//            }
//            if (state == 1 && symbol == '$') {
////                System.out.println("azaaaaaa");
//                temp += symbol;
//                state = 2;
//                inputPointer++;
//                continue;
//            }
//            if (state == 2 && (symbol >= 'a' && symbol <= 'z')) {
////                System.out.println("az");
//                state = 3;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 3) {
//                if ((symbol >= 'a' && symbol <= 'z') || (symbol >= '1' && symbol <= '9')) {
//                    state = 3;
//                    temp += symbol;
//                    inputPointer++;
//                    continue;
//                } else {
//                    output("(identifier, " + temp + " )");
//                    identifiertable.add(temp);
//                    temp = "";
//                    state = 1;
//                    continue;
//                }
//            }//identifier
//
//            if (state == 1 && symbol == ';') {
//                temp += symbol;
//                output("(seperator, " + temp + " )");
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//;
//
//            if (state == 1 && symbol == ',') {
//                temp += symbol;
//                output("(seperator, " + temp + " )");
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//,
//
//            if (state == 1 && symbol == '=') {
//                state = 6;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 6) {
//                if (symbol == '=') {
//                    temp += symbol;
//                    output("(关系运算符, " + temp + " )");
//                    state = 7;
//                    temp = "";
//                    inputPointer++;
//                    continue;
//
//                } else {
//                    output("(mathOperator, " + temp + " )");
//                    temp = "";
//                    state = 1;
//                    continue;
//                }
//            }
//
//            if (state == 1 && symbol == '<') {
//                state = 8;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 8) {
//                if (symbol == '=') {
//                    temp += symbol;
//                    inputPointer++;
//                    state=1;
//                    temp="";
//                    output("(关系运算符, " + temp + " )");
//                } else {
//                    state =1;
//                    output("(关系运算符, " + temp + " )");
//                }
//            }//<=
//
//
//            if (state == 1 && symbol == '>') {
//                state = 10;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 10) {
//                if (symbol == '=') {
//                    temp += symbol;
//                    inputPointer++;
//                    state=1;
//                    temp="";
//                    output("(关系运算符, " + temp + " )");
//                } else {
//                    state = 1;
//                    output("(关系运算符, " + temp + " )");
//                }
//            }//>=
//            if (state == 1 && symbol == '(') {
//                temp += symbol;
//                output("(mathOperator, " + temp + " )");
//                temp = "";
//                inputPointer++;
//                continue;
//            }
//            if (state == 1 && symbol == ')') {
//                temp += symbol;
//                output("(mathOperator, " + temp + " )");
//                temp = "";
//                inputPointer++;
//                continue;
//            }
//
//            if (state == 1 && Character.toString(symbol).equals("i")) {
//                state = 14;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 14 && Character.toString(symbol).equals("n")) {
//                state = 15;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 15 && Character.toString(symbol).equals("t")) {
//                temp += symbol;
//                output("(变量说明, " + temp + " )");
////                System.out.println(temp);
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//int
//
//            if (state == 14 && Character.toString(symbol).equals("f")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//if
//
//            if (state == 1 && Character.toString(symbol).equals("t")) {
//                state = 18;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 18 && Character.toString(symbol).equals("h")) {
//                state = 19;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 19 && Character.toString(symbol).equals("e")) {
//                state = 20;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 20 && Character.toString(symbol).equals("n")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//then
//
//            if (state == 1 && Character.toString(symbol).equals("e")) {
//                state = 22;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 22 && Character.toString(symbol).equals("l")) {
//                state = 23;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 23 && Character.toString(symbol).equals("s")) {
//                state = 24;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 24 && Character.toString(symbol).equals("e")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//else
//
//            if (state == 22 && Character.toString(symbol).equals("n")) {
//                state = 26;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 26 && Character.toString(symbol).equals("d")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//end
//
//            if (state == 1 && Character.toString(symbol).equals("b")) {
//                state = 28;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 28 && Character.toString(symbol).equals("e")) {
//                state = 29;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 29 && Character.toString(symbol).equals("g")) {
//                state = 30;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 30 && Character.toString(symbol).equals("i")) {
//                state = 31;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 31 && Character.toString(symbol).equals("n")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//begin
//
//            if (state == 1 && Character.toString(symbol).equals("w")) {
//                state = 33;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 33 && Character.toString(symbol).equals("h")) {
//                state = 34;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 34 && Character.toString(symbol).equals("i")) {
//                state = 35;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 35 && Character.toString(symbol).equals("l")) {
//                state = 36;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 36 && Character.toString(symbol).equals("e")) {
//                temp += symbol;
//                output("(keywords, " + temp + " )");
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//while
//
//            if (state == 1 && (symbol >= '0' && symbol <= '9')) {
//                state = 38;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 38) {
//                if (symbol >= '0' && symbol <= '9') {
//                    temp += symbol;
//                    state = 38;
//                    code += temp + " ;";//const number table
//                    inputPointer++;
//                    continue;
//                } else {
//                    output("(number, " + temp + " )");
//                    temp = "";
//                    state = 1;
//                    continue;
//                }
//            }//number
//
//            if (state == 1 && symbol == '+') {
//                temp += symbol;
//                output("(mathOperator, " + temp + " )");
//                temp = "";
//                inputPointer++;
//                continue;
//            }
//            if (state == 1 && symbol == '*') {
//                temp += symbol;
//                output("(mathOperator, " + temp + " )");
//                temp = "";
//                inputPointer++;
//                continue;
//            }
//
//            if (state == 1 && Character.toString(symbol).equals("d")) {
//                state = 41;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 41 && Character.toString(symbol).equals("o")) {
//                temp += symbol;
//                output("(keywords," + temp + " )");
//                temp = "";
//                state = 1;
//                inputPointer++;
//                continue;
//            }//do
//            if (state == 1 && symbol == '!') {
//                state = 43;
//                temp += symbol;
//                inputPointer++;
//                continue;
//            }
//            if (state == 43) {
//                if (symbol == '=') {
//                    temp += symbol;
//                    inputPointer++;
//                    state = 1;
//                    output("(关系运算符," + temp + " )");
//                }
//            }//!=
//
////            System.out.println(code);
//            output("unknown" + symbol);
//            state = 1;
//            temp = "";
//            inputPointer++;
//
//
//        }
//
//
//        output("identifier table:" + identifiertable.dump());
//        jTextArea2.append("const table:" + code);
//
//    }

    private void jButton2ActionPerformed(ActionEvent evt) throws NullPointerException {
        jTextArea2.setText("");//清空左侧输出栏
        jTextArea1.setText("");
        identifierCheckList = null;
        identifiertable.Clear();
        codeTable.Clear();
        tempVarTable.Clear();

        String d = "#";
//      System.out.println(textarea.getText());
        inputProgram = new StringBuilder(textarea.getText());
        inputProgram.append(d);
        inputPointer = 0;

        word = this.nextInput();
        output("识别单词 : " + word.toString());
        parseProgram();

        output("identifier table:" + identifiertable.dump());
        jTextArea2.append("const table:" + code + "\n");
        output("temp var table:" + tempVarTable.dump() + "\n");
        codeTable.dump(jTextArea1);

    }

    static void writeToTXT(String data) {
        try {
            FileOutputStream fileOutputStream = null;
            File file = new File("src/out.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void jButton3ActionPerformed(ActionEvent evt) throws IOException {
//        //当按钮被点击时，Swing框架会调用监听器的actionPerformed()方法
//                System.out.println("按钮被点击....");
//                String tmp = "";
//                for (fourTuple i : codeTable.table) {
//                    tmp = tmp + i.toString() + "\n";
//                }
//                writeToTXT(tmp);
//
//    }

    private void browseActionPerformed(ActionEvent evt) {
//        System.out.println("click");
        textarea.setText("");
        if (evt.getSource() == browse) {
            int returnVal = fileChooser.showOpenDialog(AnalysisDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    Long filelength = file.length();
                    byte[] filecontent = new byte[filelength.intValue()];
                    try {
                        FileInputStream in = new FileInputStream(file);
                        in.read(filecontent);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String fileStr1 = new String(filecontent);
                    textarea.append(fileStr1);

//                    FileReader fileReader = new FileReader(file);
//                    BufferedReader bufferreader = new BufferedReader(fileReader);
//                    String aline;
//                    while ((aline = bufferreader.readLine()) != null)
//                        //按行读取文本，显示在TEXTAREA中
//                        textarea.append(aline + "\r\n");
//                    fileReader.close();
//                    bufferreader.close();

                } catch (Exception o) {
                    System.out.println(o);
                }
            }
            //Handle save button action.
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AnalysisDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnalysisDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnalysisDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnalysisDemo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AnalysisDemo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private JButton browse;
    private JFileChooser fileChooser;
    //    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTextArea jTextArea1;//右侧
    private JTextArea jTextArea2;//左侧
    private JTextField jTextField1;
    private JTextArea textarea;
    // End of variables declaration
}
