import com.mathgenerator.service.ExpressionNormalizer;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author: YiLaiL
 * @date: 2025/03/13
 * @description: TODO
 */
public class NomalizerTest {
    ExpressionNormalizer normalizer = new ExpressionNormalizer();
    @Test
    public void testNormalize(){
        /*String expression = "2/4 × 1'1/5 + 1'1/5 × 1'1/5 =";
        String expression = "2/4 × 1'1/5 + 1'1/5 ÷ 1'1/5 =";
        List<String> tokenize = normalizer.tokenize(expression);
        List<String> list = normalizer.shuntingYard(tokenize);
        ExpressionNormalizer.BaseExpressionNode baseExpressionNode = normalizer.buildExpressionTree(list);
        normalizer.normalizeNode(baseExpressionNode);
        System.out.println(baseExpressionNode);
         */
        String expression = "5 + 4 + ( 3 + 2 ) × 4 =";
        List<String> tokenize = normalizer.tokenize(expression);
        System.out.println(tokenize);
        List<String> list = normalizer.shuntingYard(tokenize);
        System.out.println(list);
        ExpressionNormalizer.BaseExpressionNode baseExpressionNode = normalizer.buildExpressionTree(list);
        System.out.println(baseExpressionNode);
        String normalizeExpression = normalizer.normalizeExpression(expression);
        System.out.println(normalizeExpression);
      /*  String expression2 = "4 + 5 + ( 3 + 2 ) × 4 =";
        String normalizeExpression2 = normalizer.normalizeExpression(expression);
        System.out.println(normalizeExpression2);*/
    }
}
