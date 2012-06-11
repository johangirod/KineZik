package evaluator;

import java.io.IOException;

import modele.Song;
import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.exceptions.CannotReadException;

public class ServerSong extends Song {

	private java.io.File mp3File;

	public ServerSong(java.io.File mp3File) throws IOException, CannotReadException{
		super("A", "B", "C");
		AudioFile mp3 = AudioFileIO.read(mp3File);
		super.album = mp3.getTag().getFirstAlbum();
		super.artist = mp3.getTag().getFirstArtist();
		super.title = mp3.getTag().getFirstTitle();
		super.setGenre(mp3.getTag().getFirstGenre());
		this.mp3File = mp3File; 
	}

	public java.io.File getMp3File(){
		return this.mp3File;
	}

}
