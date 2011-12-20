import packet.Packet;
import packet.results.Result;

/**
 * Interface to provide the Observer method for when packets are received
 * @author Stephen Bussey
 *
 */
public interface Observer
{
	public void update(Packet re);
}
