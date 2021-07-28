package com.hopding.jrpicam;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.hopding.jrpicam.enums.AWB;
import com.hopding.jrpicam.enums.CameraState;
import com.hopding.jrpicam.enums.DRC;
import com.hopding.jrpicam.enums.Encoding;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.enums.ShotMode;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import com.hopding.jrpicam.helper.LanguageHelper;
import com.hopding.jrpicam.helper.SettingHelper;

public class CameraApp {
	
	private JFrame			frame;
	private JTextField		txtTimeout;
	private JButton			btnTake;
	private JButton			btnSave;
	private BufferedImage	buffImg;	
	JLabel imageLabel;
	JComboBox<TranslatedEnum<ShotMode>> shotModeComboBox;
	JComboBox<TranslatedEnum<Encoding>> encComboBox;
	JComboBox<TranslatedEnum<AWB>> awbComboBox;
	JComboBox<TranslatedEnum<DRC>> drcComboBox;
	JComboBox<TranslatedEnum<Exposure>> expComboBox;
	JSlider contrastSlider;
	JSlider qualitySlider;
	JSlider sharpnessSlider;
	
	private RPiCamera		piCamera;
	
	private CameraState cameraState;
	int imageWidth = 575;
    int imageHeight = 565;
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	    SettingHelper.init();
	    TranslatedEnum<AWB> test = new TranslatedEnum<>(AWB.AUTO);
	    System.out.println(test.toString());
	    
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CameraApp window = new CameraApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public CameraApp() {
		try {
			piCamera = new RPiCamera("/home/pi/Pictures");
		} catch (FailedToRunRaspistillException e) {
			e.printStackTrace();
		}
		initialize();
	}
	
	
    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> TranslatedEnum<T>[] translatedEnums(T[] values) {
	    List<TranslatedEnum<T>> list = new ArrayList<>();
	    for (T value : values) {
	        list.add(new TranslatedEnum<T>(value));
	    }
        return list.toArray((TranslatedEnum<T>[]) new TranslatedEnum[]{});
	}
	
	static class TranslatedEnum<T extends Enum<T>> {

	    
        private final T value;
        
        public TranslatedEnum(T value) {
            this.value = value;
        }
        
        public T getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            String translateId = value.getDeclaringClass().getSimpleName() + "." + value.name();
            return LanguageHelper.tanslate(translateId);
        }
	}
	
	/**
	 * 指定图片区域的长宽；按钮区域宽度：图片区域宽度 保持为 4:1
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    int imgAreaWidth = 400;
	    int imgAreaHeight = 300;
	    int settingsAreaWidth = 200;
	    
	    
	    GridBagConstraints panelGbc = new GridBagConstraints();
	    panelGbc.fill = GridBagConstraints.BOTH;
	    
		frame = new JFrame();
		frame.setBounds(0, 0, imgAreaWidth + settingsAreaWidth, imgAreaHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridBagLayout());
		
		imageLabel = new JLabel();
		imageLabel.setBackground(Color.BLUE);
		imageLabel.setOpaque(true);
		imageLabel.setText("imageLabel");
		panelGbc.gridx = 0;
		panelGbc.gridy = 0;
		//panelGbc.gridwidth = 10;
		panelGbc.weightx = 1;
		panelGbc.weighty = 1;
		frame.getContentPane().add(imageLabel, panelGbc);
		
		
		JPanel settingPanel = new JPanel();
		//settingPanel.setSize(150, 480);
		settingPanel.setLayout(new GridBagLayout());
		panelGbc.gridx = 1;
        panelGbc.gridy = 0;
        //panelGbc.gridwidth = 1;
        panelGbc.weightx = 0;
        panelGbc.weighty = 0;
		frame.getContentPane().add(settingPanel, panelGbc);
		
		// ====== settings area ======
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
        final int descriptionX = 0;
        final int inputX = 1;
        final int buttonX = 2;
        final Font descriptionFont = new Font("SimSun", Font.BOLD, 11);
        gbc.gridy = -1;
        
        // ------ line -----
        gbc.gridy++;
        
        Label labelTimeout = new Label("Timeout:\r\n");
        labelTimeout.setFont(descriptionFont);
        gbc.gridx = descriptionX;
        settingPanel.add(labelTimeout, gbc);
        
		txtTimeout = new JTextField();
		txtTimeout.setText("0");
		txtTimeout.setColumns(10);
		gbc.gridx = inputX;
        settingPanel.add(txtTimeout, gbc);
		
        // ------ line -----
        gbc.gridy++;
        
        JLabel lblEncoding = new JLabel(LanguageHelper.tanslate(Encoding.class.getSimpleName()));
        lblEncoding.setFont(descriptionFont);
        gbc.gridx = descriptionX;
        settingPanel.add(lblEncoding, gbc);
        
        TranslatedEnum<Encoding>[] encComboBoxChoices = translatedEnums(Encoding.values());
		encComboBox = new JComboBox<>(encComboBoxChoices);
		gbc.gridx = inputX;
        settingPanel.add(encComboBox, gbc);
		
        // ------ line -----
        gbc.gridy++;
        
		Label label = new Label(LanguageHelper.tanslate(AWB.class.getSimpleName()));
		label.setFont(descriptionFont);
		gbc.gridx = descriptionX;
		settingPanel.add(label, gbc);
		
		TranslatedEnum<AWB>[] awbComboBoxChoices = translatedEnums(AWB.values());
        awbComboBox = new JComboBox<>(awbComboBoxChoices);
        gbc.gridx = inputX;
        settingPanel.add(awbComboBox, gbc);
		
        // ------ line -----
        gbc.gridy++;
        
		JLabel lblNewLabel = new JLabel(LanguageHelper.tanslate(DRC.class.getSimpleName()));
		lblNewLabel.setFont(descriptionFont);
		gbc.gridx = descriptionX;
		settingPanel.add(lblNewLabel, gbc);
		
		TranslatedEnum<DRC>[] drcComboBoxChoices = translatedEnums(DRC.values());
		drcComboBox = new JComboBox<>(drcComboBoxChoices);
		gbc.gridx = inputX;
		settingPanel.add(drcComboBox, gbc);
		
		// ------ line -----
        gbc.gridy++;
        
		Label label_1 = new Label(LanguageHelper.tanslate(Exposure.class.getSimpleName()));
		label_1.setFont(descriptionFont);
		gbc.gridx = descriptionX;
		settingPanel.add(label_1, gbc);
		
		TranslatedEnum<Exposure>[] expComboBoxChoices = translatedEnums(Exposure.values());
		expComboBox = new JComboBox<>(expComboBoxChoices);
		gbc.gridx = inputX;
		settingPanel.add(expComboBox, gbc);
		
        // ------ line -----
        gbc.gridy++;
		
        JLabel lblContrast = new JLabel(LanguageHelper.tanslate("Contrast"));
        gbc.gridx = descriptionX;
        settingPanel.add(lblContrast, gbc);
        
		contrastSlider = new JSlider();
		contrastSlider.setMinimum(-100);
		gbc.gridx = inputX;
		settingPanel.add(contrastSlider, gbc);
		
		// ------ line -----
        gbc.gridy++;
		
		JLabel lblQuality = new JLabel(LanguageHelper.tanslate("Quality"));
		gbc.gridx = descriptionX;
		settingPanel.add(lblQuality, gbc);
		
		qualitySlider = new JSlider();
		qualitySlider.setValue(75);
		gbc.gridx = inputX;
		settingPanel.add(qualitySlider, gbc);
		
		// ------ line -----
        gbc.gridy++;
		
		JLabel lblSharpness = new JLabel(LanguageHelper.tanslate("Sharpness"));
		gbc.gridx = descriptionX;
		settingPanel.add(lblSharpness, gbc);
		
		sharpnessSlider = new JSlider();
		sharpnessSlider.setValue(0);
		sharpnessSlider.setMinimum(-100);
		gbc.gridx = inputX;
		settingPanel.add(sharpnessSlider, gbc);
		
		// ------ line -----
        gbc.gridy++;
        
        Label labelShotMode = new Label(LanguageHelper.tanslate(ShotMode.class.getSimpleName()));
        labelShotMode.setFont(descriptionFont);
        gbc.gridx = descriptionX;
        settingPanel.add(labelShotMode, gbc);
        
        TranslatedEnum<ShotMode>[] shotModeComboBoxChoices = translatedEnums(ShotMode.values());
        shotModeComboBox = new JComboBox<>(shotModeComboBoxChoices);
        gbc.gridx = inputX;
        settingPanel.add(shotModeComboBox, gbc);
		
		// ====== button area ======
		gbc.gridy = 0;
		gbc.gridx = buttonX;
		
        // ------ line -----
        gbc.gridy++;
		btnTake = new JButton(LanguageHelper.tanslate("Take"));
		btnTake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//			    if (cameraState != CameraState.IDEL) {
//                    JOptionPane.showMessageDialog(null, "Cannot take in sate: " + cameraState);
//                    return;
//                }
			    if (cameraState != CameraState.IDEL && cameraState != CameraState.WAIT_SAVE) {
			        Encoding encoding = encComboBox.getItemAt(encComboBox.getSelectedIndex()).value;
			        save(LocalDateTime.now().toString(), encoding);
                }
			    
			    takeToBuffer();
			}

            
		});
		//btnTake.setBounds(585, 11, 89, 23);
		gbc.gridx = buttonX;
        gbc.gridy = 0;
		settingPanel.add(btnTake, gbc);
		
//		KeyboardFocusManager.getCurrentKeyboardFocusManager()
//		  .addKeyEventDispatcher(new KeyEventDispatcher() {
//		      @Override
//		      public boolean dispatchKeyEvent(KeyEvent e) {
//    	          if (cameraState == CameraState.PREVIWING) {
//    	              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//    	                  takeAndUpdateUI();
//    	              } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//    	                  takeAndUpdateUI();
//    	                  setCameraStateAndUpdateUI(CameraState.IDEL);
//    	              }
//    	          }
//    	          return false;
//		      }
//		});
		
		// ------ line -----
        gbc.gridy++;
		btnSave = new JButton(LanguageHelper.tanslate("Save"));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			    if (cameraState != CameraState.WAIT_SAVE) {
			        JOptionPane.showMessageDialog(null, "Cannot save in sate: " + cameraState);
			        return;
			    }
				JFileChooser fileChooser = new JFileChooser();
				Encoding encoding = encComboBox.getItemAt(encComboBox.getSelectedIndex()).value;
				if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					save(fileChooser.getSelectedFile().getAbsolutePath(), encoding);
				}
			}
		});
		//btnSave.setBounds(585, 34, 89, 23);
		//btnSave.setEnabled(false);
		settingPanel.add(btnSave, gbc);
		
		
		setCameraStateAndUpdateUI(CameraState.IDEL);
	}
	
	private void setCameraStateAndUpdateUI(CameraState newState) {
	    cameraState = newState;
	    frame.setTitle(cameraState.name());
	}
	
	private void takeToBuffer() {
	    Encoding encoding = encComboBox.getItemAt(encComboBox.getSelectedIndex()).value;
	    ShotMode shotMode = shotModeComboBox.getItemAt(shotModeComboBox.getSelectedIndex()).value;
        switch (shotMode) {
            case ONE_PREVIEW_SHOT:
                piCamera.turnOnPreview(); 
                break;
            default:
                piCamera.turnOffPreview();
                break;
        }
        
        try {
            int imageWidth = 575;
            int imageHeight = 565;
            piCamera
                .setAWB(awbComboBox.getItemAt(awbComboBox.getSelectedIndex()).value)
                .setDRC(drcComboBox.getItemAt(drcComboBox.getSelectedIndex()).value)
                .setExposure(expComboBox.getItemAt(expComboBox.getSelectedIndex()).value)
                .setEncoding(encoding)
                .setWidth(imageWidth)
                .setHeight(imageHeight)
                .setContrast(contrastSlider.getValue())
                .setQuality(qualitySlider.getValue())
                .setSharpness(sharpnessSlider.getValue())
                .setTimeout(Integer.parseInt(txtTimeout.getText()));
            
            buffImg = piCamera.takeBufferedStill();
            System.out.println("Executed this command:\n\t" + piCamera.getPrevCommand());
            double scaleRate = Math.min(imageLabel.getWidth() * 1.0 / imageWidth , imageLabel.getHeight() * 1.0 / imageHeight);
            Image scaled = buffImg.getScaledInstance((int)(imageWidth * scaleRate), (int)(imageHeight * scaleRate), Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            imageLabel.setIcon(icon);

            setCameraStateAndUpdateUI(CameraState.WAIT_SAVE);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Please Enter a Value for Timeout.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void save(String filePathWithoutEncoding, Encoding encoding) {
	    String saveFilePath = filePathWithoutEncoding +
                "." + encoding.toString();
        File saveFile = new File(saveFilePath);
        try {
            ImageIO.write(buffImg, encoding.toString(), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffImg = null;
        imageLabel.setIcon(null);
        
    }
	


}
