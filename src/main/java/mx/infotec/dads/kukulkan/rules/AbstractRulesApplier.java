/*
 *  
 * The MIT License (MIT)
 * Copyright (c) 2018 Roberto Villarejo Martínez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mx.infotec.dads.kukulkan.rules;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

public abstract class AbstractRulesApplier implements RulesApplier {

    private final Logger logger = LoggerFactory.getLogger(DefaultRulesApplier.class);

    protected List<Rule> rules;

    private ExpressionParser parser;

    public AbstractRulesApplier(List<Rule> rules, ExpressionParser parser) {
        this.rules = rules;
        this.parser = parser;
    }

    /**
     * Evaluate rules one by one and do actions if passed condition
     */
    @Override
    public void apply(EvaluationContext context) {
        for (Rule rule : rules) {
            logger.info("Checking condition for rule: {}", rule.getName());
            if (evaluateCondition(rule, context)) {
                logger.info("Applying rule: {}", rule.getName());
                doActions(rule, context);
            } else {
                logger.info("Rule {} not applied", rule.getName());
            }
        }
    }

    protected void doActions(Rule rule, EvaluationContext context) {
        for (Action action : rule.getActions()) {
            logger.info("Doing action: {}", action.getAction());
            Expression propertyExpression = parser.parseExpression(action.getPropertyExpression());
            Class<?> clazz = propertyExpression.getValueTypeDescriptor(context).getType();
            parser.parseExpression(action.getPropertyExpression()).setValue(context,
                    clazz.cast(parser.parseExpression(action.getValueExpression()).getValue(context)));
        }
    }

    protected boolean evaluateCondition(Rule rule, EvaluationContext context) {
        try {
            return (boolean) parser.parseExpression(rule.getCondition()).getValue(context);
        } catch (EvaluationException ex) {
            logger.error("Failed to evaluate condition: {} from rule: {}", rule.getCondition(), rule.getName());
            return false;
        }
    }

}
