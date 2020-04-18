/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ali
 */
public class SyntaxErrorListener extends BaseErrorListener {

    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    public SyntaxErrorListener() {
    }

    public List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line, int charPositionInLine,
            String msg, RecognitionException e) {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
    }

    @Override
    public String toString() {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }
}
