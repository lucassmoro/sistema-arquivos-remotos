import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: sistema_arquivos.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class SistemaArquivosGrpc {

  private SistemaArquivosGrpc() {}

  public static final java.lang.String SERVICE_NAME = "SistemaArquivos";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SistemaArquivosOuterClass.AbreRequest,
      SistemaArquivosOuterClass.AbreReply> getAbreMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Abre",
      requestType = SistemaArquivosOuterClass.AbreRequest.class,
      responseType = SistemaArquivosOuterClass.AbreReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SistemaArquivosOuterClass.AbreRequest,
      SistemaArquivosOuterClass.AbreReply> getAbreMethod() {
    io.grpc.MethodDescriptor<SistemaArquivosOuterClass.AbreRequest, SistemaArquivosOuterClass.AbreReply> getAbreMethod;
    if ((getAbreMethod = SistemaArquivosGrpc.getAbreMethod) == null) {
      synchronized (SistemaArquivosGrpc.class) {
        if ((getAbreMethod = SistemaArquivosGrpc.getAbreMethod) == null) {
          SistemaArquivosGrpc.getAbreMethod = getAbreMethod =
              io.grpc.MethodDescriptor.<SistemaArquivosOuterClass.AbreRequest, SistemaArquivosOuterClass.AbreReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Abre"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.AbreRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.AbreReply.getDefaultInstance()))
              .setSchemaDescriptor(new SistemaArquivosMethodDescriptorSupplier("Abre"))
              .build();
        }
      }
    }
    return getAbreMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SistemaArquivosOuterClass.LeRequest,
      SistemaArquivosOuterClass.LeReply> getLeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Le",
      requestType = SistemaArquivosOuterClass.LeRequest.class,
      responseType = SistemaArquivosOuterClass.LeReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SistemaArquivosOuterClass.LeRequest,
      SistemaArquivosOuterClass.LeReply> getLeMethod() {
    io.grpc.MethodDescriptor<SistemaArquivosOuterClass.LeRequest, SistemaArquivosOuterClass.LeReply> getLeMethod;
    if ((getLeMethod = SistemaArquivosGrpc.getLeMethod) == null) {
      synchronized (SistemaArquivosGrpc.class) {
        if ((getLeMethod = SistemaArquivosGrpc.getLeMethod) == null) {
          SistemaArquivosGrpc.getLeMethod = getLeMethod =
              io.grpc.MethodDescriptor.<SistemaArquivosOuterClass.LeRequest, SistemaArquivosOuterClass.LeReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Le"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.LeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.LeReply.getDefaultInstance()))
              .setSchemaDescriptor(new SistemaArquivosMethodDescriptorSupplier("Le"))
              .build();
        }
      }
    }
    return getLeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SistemaArquivosOuterClass.EscreveRequest,
      SistemaArquivosOuterClass.EscreveReply> getEscreveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Escreve",
      requestType = SistemaArquivosOuterClass.EscreveRequest.class,
      responseType = SistemaArquivosOuterClass.EscreveReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SistemaArquivosOuterClass.EscreveRequest,
      SistemaArquivosOuterClass.EscreveReply> getEscreveMethod() {
    io.grpc.MethodDescriptor<SistemaArquivosOuterClass.EscreveRequest, SistemaArquivosOuterClass.EscreveReply> getEscreveMethod;
    if ((getEscreveMethod = SistemaArquivosGrpc.getEscreveMethod) == null) {
      synchronized (SistemaArquivosGrpc.class) {
        if ((getEscreveMethod = SistemaArquivosGrpc.getEscreveMethod) == null) {
          SistemaArquivosGrpc.getEscreveMethod = getEscreveMethod =
              io.grpc.MethodDescriptor.<SistemaArquivosOuterClass.EscreveRequest, SistemaArquivosOuterClass.EscreveReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Escreve"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.EscreveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.EscreveReply.getDefaultInstance()))
              .setSchemaDescriptor(new SistemaArquivosMethodDescriptorSupplier("Escreve"))
              .build();
        }
      }
    }
    return getEscreveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SistemaArquivosOuterClass.FechaRequest,
      SistemaArquivosOuterClass.FechaReply> getFechaMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Fecha",
      requestType = SistemaArquivosOuterClass.FechaRequest.class,
      responseType = SistemaArquivosOuterClass.FechaReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SistemaArquivosOuterClass.FechaRequest,
      SistemaArquivosOuterClass.FechaReply> getFechaMethod() {
    io.grpc.MethodDescriptor<SistemaArquivosOuterClass.FechaRequest, SistemaArquivosOuterClass.FechaReply> getFechaMethod;
    if ((getFechaMethod = SistemaArquivosGrpc.getFechaMethod) == null) {
      synchronized (SistemaArquivosGrpc.class) {
        if ((getFechaMethod = SistemaArquivosGrpc.getFechaMethod) == null) {
          SistemaArquivosGrpc.getFechaMethod = getFechaMethod =
              io.grpc.MethodDescriptor.<SistemaArquivosOuterClass.FechaRequest, SistemaArquivosOuterClass.FechaReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Fecha"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.FechaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SistemaArquivosOuterClass.FechaReply.getDefaultInstance()))
              .setSchemaDescriptor(new SistemaArquivosMethodDescriptorSupplier("Fecha"))
              .build();
        }
      }
    }
    return getFechaMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SistemaArquivosStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosStub>() {
        @java.lang.Override
        public SistemaArquivosStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SistemaArquivosStub(channel, callOptions);
        }
      };
    return SistemaArquivosStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SistemaArquivosBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosBlockingStub>() {
        @java.lang.Override
        public SistemaArquivosBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SistemaArquivosBlockingStub(channel, callOptions);
        }
      };
    return SistemaArquivosBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SistemaArquivosFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SistemaArquivosFutureStub>() {
        @java.lang.Override
        public SistemaArquivosFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SistemaArquivosFutureStub(channel, callOptions);
        }
      };
    return SistemaArquivosFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void abre(SistemaArquivosOuterClass.AbreRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.AbreReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAbreMethod(), responseObserver);
    }

    /**
     */
    default void le(SistemaArquivosOuterClass.LeRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.LeReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLeMethod(), responseObserver);
    }

    /**
     */
    default void escreve(SistemaArquivosOuterClass.EscreveRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.EscreveReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEscreveMethod(), responseObserver);
    }

    /**
     */
    default void fecha(SistemaArquivosOuterClass.FechaRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.FechaReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFechaMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SistemaArquivos.
   */
  public static abstract class SistemaArquivosImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return SistemaArquivosGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SistemaArquivos.
   */
  public static final class SistemaArquivosStub
      extends io.grpc.stub.AbstractAsyncStub<SistemaArquivosStub> {
    private SistemaArquivosStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SistemaArquivosStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SistemaArquivosStub(channel, callOptions);
    }

    /**
     */
    public void abre(SistemaArquivosOuterClass.AbreRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.AbreReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAbreMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void le(SistemaArquivosOuterClass.LeRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.LeReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void escreve(SistemaArquivosOuterClass.EscreveRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.EscreveReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEscreveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fecha(SistemaArquivosOuterClass.FechaRequest request,
        io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.FechaReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFechaMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SistemaArquivos.
   */
  public static final class SistemaArquivosBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SistemaArquivosBlockingStub> {
    private SistemaArquivosBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SistemaArquivosBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SistemaArquivosBlockingStub(channel, callOptions);
    }

    /**
     */
    public SistemaArquivosOuterClass.AbreReply abre(SistemaArquivosOuterClass.AbreRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAbreMethod(), getCallOptions(), request);
    }

    /**
     */
    public SistemaArquivosOuterClass.LeReply le(SistemaArquivosOuterClass.LeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLeMethod(), getCallOptions(), request);
    }

    /**
     */
    public SistemaArquivosOuterClass.EscreveReply escreve(SistemaArquivosOuterClass.EscreveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEscreveMethod(), getCallOptions(), request);
    }

    /**
     */
    public SistemaArquivosOuterClass.FechaReply fecha(SistemaArquivosOuterClass.FechaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFechaMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SistemaArquivos.
   */
  public static final class SistemaArquivosFutureStub
      extends io.grpc.stub.AbstractFutureStub<SistemaArquivosFutureStub> {
    private SistemaArquivosFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SistemaArquivosFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SistemaArquivosFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SistemaArquivosOuterClass.AbreReply> abre(
        SistemaArquivosOuterClass.AbreRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAbreMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SistemaArquivosOuterClass.LeReply> le(
        SistemaArquivosOuterClass.LeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SistemaArquivosOuterClass.EscreveReply> escreve(
        SistemaArquivosOuterClass.EscreveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEscreveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SistemaArquivosOuterClass.FechaReply> fecha(
        SistemaArquivosOuterClass.FechaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFechaMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ABRE = 0;
  private static final int METHODID_LE = 1;
  private static final int METHODID_ESCREVE = 2;
  private static final int METHODID_FECHA = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ABRE:
          serviceImpl.abre((SistemaArquivosOuterClass.AbreRequest) request,
              (io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.AbreReply>) responseObserver);
          break;
        case METHODID_LE:
          serviceImpl.le((SistemaArquivosOuterClass.LeRequest) request,
              (io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.LeReply>) responseObserver);
          break;
        case METHODID_ESCREVE:
          serviceImpl.escreve((SistemaArquivosOuterClass.EscreveRequest) request,
              (io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.EscreveReply>) responseObserver);
          break;
        case METHODID_FECHA:
          serviceImpl.fecha((SistemaArquivosOuterClass.FechaRequest) request,
              (io.grpc.stub.StreamObserver<SistemaArquivosOuterClass.FechaReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getAbreMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              SistemaArquivosOuterClass.AbreRequest,
              SistemaArquivosOuterClass.AbreReply>(
                service, METHODID_ABRE)))
        .addMethod(
          getLeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              SistemaArquivosOuterClass.LeRequest,
              SistemaArquivosOuterClass.LeReply>(
                service, METHODID_LE)))
        .addMethod(
          getEscreveMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              SistemaArquivosOuterClass.EscreveRequest,
              SistemaArquivosOuterClass.EscreveReply>(
                service, METHODID_ESCREVE)))
        .addMethod(
          getFechaMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              SistemaArquivosOuterClass.FechaRequest,
              SistemaArquivosOuterClass.FechaReply>(
                service, METHODID_FECHA)))
        .build();
  }

  private static abstract class SistemaArquivosBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SistemaArquivosBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SistemaArquivosOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SistemaArquivos");
    }
  }

  private static final class SistemaArquivosFileDescriptorSupplier
      extends SistemaArquivosBaseDescriptorSupplier {
    SistemaArquivosFileDescriptorSupplier() {}
  }

  private static final class SistemaArquivosMethodDescriptorSupplier
      extends SistemaArquivosBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    SistemaArquivosMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SistemaArquivosGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SistemaArquivosFileDescriptorSupplier())
              .addMethod(getAbreMethod())
              .addMethod(getLeMethod())
              .addMethod(getEscreveMethod())
              .addMethod(getFechaMethod())
              .build();
        }
      }
    }
    return result;
  }
}
