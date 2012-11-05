package ca.t.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ParsedDocument {

	private String id;
	
	private String assetType;
	private String fullAssetType;
	private String publishDate;
	private String title;
	private String _abstract;
	private String body;
	private List<String> categories = new ArrayList<String>();

	private Set<String> openNlpNames = new TreeSet<String>();
	private Set<String> openNlpOrganizations = new TreeSet<String>();
	private Set<String> openNlpLocations = new TreeSet<String>();

	private Set<String> calaisEntities = new TreeSet<String>();
	private Set<String> calaisTopics = new TreeSet<String>();
	private Set<String> calaisTags = new TreeSet<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getCalaisEntities() {
		return calaisEntities;
	}

	public void setCalaisEntities(Set<String> calaisEntities) {
		this.calaisEntities = calaisEntities;
	}

	public Set<String> getCalaisTopics() {
		return calaisTopics;
	}

	public void setCalaisTopics(Set<String> calaisTopics) {
		this.calaisTopics = calaisTopics;
	}

	public Set<String> getCalaisTags() {
		return calaisTags;
	}

	public void setCalaisTags(Set<String> calaisTags) {
		this.calaisTags = calaisTags;
	}

	public Set<String> getOpenNlpNames() {
		return openNlpNames;
	}

	public void setOpenNlpNames(Set<String> openNlpNames) {
		this.openNlpNames = openNlpNames;
	}

	public Set<String> getOpenNlpOrganizations() {
		return openNlpOrganizations;
	}

	public void setOpenNlpOrganizations(Set<String> openNlpOrganizations) {
		this.openNlpOrganizations = openNlpOrganizations;
	}

	public Set<String> getOpenNlpLocations() {
		return openNlpLocations;
	}

	public void setOpenNlpLocations(Set<String> openNlpLocations) {
		this.openNlpLocations = openNlpLocations;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getFullAssetType() {
		return fullAssetType;
	}

	public void setFullAssetType(String fullAssetType) {
		this.fullAssetType = fullAssetType;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstract() {
		return _abstract;
	}

	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public void addCategory(String category) {
		this.categories.add(category);
	}

	public void addOpenNlpLocation(String location) {
		openNlpLocations.add(location);
	}

	public void addOpenNlpOrganization(String organization) {
		openNlpLocations.add(organization);
	}

	public void addOpenNlpName(String name) {
		openNlpNames.add(name);
	}

	public void addCalaisEntity(String entity) {
		calaisEntities.add(entity);
	}

	public void addCalaisTag(String tag) {
		calaisTags.add(tag);
	}

	public void addCalaisTopic(String topic) {
		calaisTopics.add(topic);
	}

}
