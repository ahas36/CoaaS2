package au.coaas.grpc.utils;

public enum Services {
    CPREE(9292),
    CQC(8484),
    CQP(8585),
    CRE(8583),
    CSI(8582),
    SQEM(8686),
    SVM(9191),
    RWC(6868);

    public final int port;
    private Services(int port) {
        this.port = port;
    }
}
