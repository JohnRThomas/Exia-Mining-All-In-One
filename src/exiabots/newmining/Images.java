package exiabots.newmining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.runemate.game.api.hybrid.Environment;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Images {
	ImageView styleImage = null;
	ImageView locationImage = null;
	ImageView startImage = null;
	ImageView closeImage = null;

	public Images(){
		try{
			styleImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/style.png"))));
			locationImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/location.png"))));
			startImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/start.png"))));
			closeImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/close.png"))));
			//warnImage = new ImageView(new Image(new FileInputStream(new File(Environment.getStorageDirectory() + "/warning.png"))));
		}catch(FileNotFoundException e){
			try {
				Image saveAs = new Image(new URL("http://i.imgur.com/WmZ6KYL.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/style.png"));
				styleImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/ws9xyTd.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/location.png"));
				locationImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/wdFM8s2.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/start.png"));
				startImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/60mcBHM.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/close.png"));
				closeImage = new ImageView(saveAs);

				saveAs = new Image(new URL("http://i.imgur.com/4bOrdWf.png").openStream());
				ImageIO.write(SwingFXUtils.fromFXImage(saveAs, null), "png", new File(Environment.getStorageDirectory() + "/warning.png"));
				//warnImage = new ImageView(saveAs);

			} catch (IOException ex) {
				System.out.println("Failed to Read Files from web!");
				ex.printStackTrace();
			}
		}

	}
}
