package tf.cyber.resourcemanager.pep.mapper.toxacml;

public class IntegerValueMapper implements JavaToXACMLMapper<Integer> {
    @Override
    public String map(Integer obj) {
        return obj.toString();
    }
}
