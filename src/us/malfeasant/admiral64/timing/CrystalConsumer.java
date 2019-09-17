package us.malfeasant.admiral64.timing;

public interface CrystalConsumer {	// TODO: Possibly needs more methods than just cycle()-
	// For instance, goHigh(), goLow(), or maybe even a 4-step cycle like 
	// posEdge(), posLevel(), negEdge(), negLevel()...
	// Or even a partCycle(CyclePart part) or similar...  need to analyze behavior
	void cycle();
}
