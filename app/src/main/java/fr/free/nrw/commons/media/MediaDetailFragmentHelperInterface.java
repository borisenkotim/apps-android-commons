package fr.free.nrw.commons.media;

import fr.free.nrw.commons.Media;

public interface MediaDetailFragmentHelperInterface {

  public String prettyCoordinates(Media media);

  public String prettyCaption(Media media);

  public String chooseDescription(Media media);

  public String prettyDescription(Media media);

  public String prettyLicense(Media media);

  public String prettyUploadedDate(Media media);
}
