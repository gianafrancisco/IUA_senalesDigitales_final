class FIR implements Filter {
    private int length;
    private double[] delayLine;
    private double[] impulseResponse;
    private int count = 0;

    public FIR() {
        this(Filter.LOW_PASS_2K_44100);
    }

    public FIR(double[] coefs) {
        length = coefs.length;
        impulseResponse = coefs;
        delayLine = new double[length];
    }

    public double apply(double value) {
        delayLine[count] = value;
        double result = 0.0;
        int index = count;
        for (int i=0; i<length; i++) {
                result += impulseResponse[i] * delayLine[index--];
              if (index < 0) index = length-1;
            }
        if (++count >= length) count = 0;
        return result;
    }
}