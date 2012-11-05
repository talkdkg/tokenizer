package ca.t.config;

import java.util.List;

public class Config {

	private List<SeedConfig> seeds;
	private List<HostConfig> hosts;

	public List<HostConfig> getHosts() {
		return hosts;
	}

	public void setHosts(List<HostConfig> hosts) {
		this.hosts = hosts;
	}

	public List<SeedConfig> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<SeedConfig> seeds) {
		this.seeds = seeds;
	}


}
