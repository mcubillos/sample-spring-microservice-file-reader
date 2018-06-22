package org.process.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public class PDIFileCsvReader implements FileReaderService {

	private static String STEP_READ_A_FILE = "Read a file";
	private static String STEP_DUMMY = "Dummy";

	private String filename;
	private InputStream inputStream;
	private TextFileInputField[] inputFields;
	private List<RowMetaAndData> rows;

	public PDIFileCsvReader(String filename, InputStream inputStream) {
		this.filename = filename;
		this.inputStream = inputStream;
	}

	public void transformData() {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		List<String> columns;
		try {
			columns = Arrays.asList(br.readLine().split(","));
			List<TextFileInputField> textFileInputFiled = new ArrayList<TextFileInputField>();
			
			columns.forEach(col -> {
				textFileInputFiled.add(new TextFileInputField(col, -1, 50));
			});
			
			inputFields = new TextFileInputField[textFileInputFiled.size()];
			
			inputFields = textFileInputFiled.toArray(inputFields);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void read() {
		try {
			KettleEnvironment.init();

			TransMeta transMeta = new TransMeta();
			transMeta.setName(filename);

			CsvInputMeta inputMeta = new CsvInputMeta();
			inputMeta.setDefault();
			inputMeta.setFilename(filename);
			inputMeta.setInputFields(inputFields);

			StepMeta inputStep = new StepMeta(STEP_READ_A_FILE, inputMeta);
			inputStep.setLocation(50, 50);
			inputStep.setDraw(true);
			transMeta.addStep(inputStep);

			DummyTransMeta dummyMeta = new DummyTransMeta();
			StepMeta dummyStep = new StepMeta(STEP_DUMMY, dummyMeta);
			dummyStep.setLocation(150, 50);
			dummyStep.setDraw(true);
			transMeta.addStep(dummyStep);

			TransHopMeta hop = new TransHopMeta(inputStep, dummyStep);
			transMeta.addTransHop(hop);

			Trans trans = new Trans(transMeta);
			trans.prepareExecution(null);

			rows = new ArrayList<RowMetaAndData>();

			RowListener rowListener = new RowAdapter() {
				public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
					rows.add(new RowMetaAndData(rowMeta, row));
				}
			};

			StepInterface setpInterface = trans.findRunThread(STEP_DUMMY);
			setpInterface.addRowListener(rowListener);

			trans.startThreads();
			trans.waitUntilFinished();

			if (trans.getErrors() > 0) {
				System.out.println("We read " + rows.size() + " rows from step " + STEP_DUMMY);
			} else {
				System.out.println("Error");
			}
		} catch (KettleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<RowMetaAndData> getRoows() {
		return rows;
	}

	@Override
	public Map<String, Map<Integer, CellInfo>> getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
