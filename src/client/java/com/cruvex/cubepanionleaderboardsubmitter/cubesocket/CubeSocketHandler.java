package com.cruvex.cubepanionleaderboardsubmitter.cubesocket;

import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline.PacketDecoder;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline.PacketEncoder;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline.PacketPrepender;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.pipeline.PacketSplitter;
import com.cruvex.cubepanionleaderboardsubmitter.cubesocket.protocol.PacketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;

public class CubeSocketHandler extends ChannelInitializer<NioSocketChannel> {

  private final CubeSocket cubeSocket;
  private final PacketHandler handler;

  private NioSocketChannel channel;

  public CubeSocketHandler(CubeSocket cubeSocket, PacketHandler handler) {
    this.cubeSocket = cubeSocket;
    this.handler = handler;
  }

  @Override
  protected void initChannel(NioSocketChannel channel) {
    this.channel = channel;
    channel.pipeline()
        .addLast("timeout", new ReadTimeoutHandler(30L, TimeUnit.SECONDS))
        .addLast("splitter", new PacketSplitter())
        .addLast("decoder", new PacketDecoder(this.cubeSocket))
        .addLast("prepender", new PacketPrepender())
        .addLast("encoder", new PacketEncoder(this.cubeSocket))
        .addLast(this.handler);
  }

  public NioSocketChannel getChannel() {
    return this.channel;
  }
}
