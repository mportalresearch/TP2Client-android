package com.mportal.siputil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mportal.logger.SIPLogger;

public class FileUtil
{

    private static final String TAG = "TWC::FileUtil";

    public static byte[] readInputStream(InputStream is) throws Exception
    {

	byte[] data = null;
	if (is == null)
	{
	    return data;
	}
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	int lengthRead = -1;
	byte[] bufRead = new byte[2048];
	try
	{
	    while ((lengthRead = is.read(bufRead)) != -1)
	    {
		baos.write(bufRead, 0, lengthRead);
	    }
	    data = baos.toByteArray();
	}
	catch (Exception e)
	{
	    throw e;
	}
	finally
	{
	    if (baos != null)
	    {
		baos.close();
	    }
	    is.close();
	}

	return data;
    }

    public static boolean copyDirectory(File srcDir, File destDir) throws IOException
    {

	SIPLogger.d(TAG, "copyDirectory started from = " + srcDir + " Dest = destDir" + destDir);
	if (srcDir == null || destDir == null)
	{
	    return false;
	}
	destDir.mkdirs();
	File[] srcFiles = srcDir.listFiles();
	if (srcFiles != null && srcFiles.length > 0)
	{
	    boolean nRet = false;
	    for (int i = 0; i < srcFiles.length; i++)
	    {
		if (srcFiles[i].isDirectory())
		{
		    String dirName = srcFiles[i].getName();
		    String destDirName = destDir.getAbsolutePath() + "/" + dirName;
		    File destFolder = new File(destDirName);
		    nRet = copyDirectory(srcFiles[i], destFolder);
		}
		else
		{
		    String destFileName = destDir.getAbsolutePath() + "/" + srcFiles[i].getName();
		    File destFile = new File(destFileName);
		    if (destFile.exists())
		    {
			destFile.delete();
		    }
		    if (!destFile.exists())
		    {
			destFile.createNewFile();
		    }
		    nRet = copyFile(srcFiles[i], destFile);
		}
	    }
	    return nRet;
	}
	return false;
    }

    public static boolean writeFile(byte[] data, String path, String filename) throws FileNotFoundException, IOException
    {
	if (data == null)
	{
	    return false;
	}
	File dstFile = new File(path, filename);
	SIPLogger.d(TAG, "WriteFile to Dest = destDir" + dstFile);
	if (!dstFile.exists())
	{
	    dstFile.createNewFile();
	}
	if (dstFile.exists())
	{
	    FileOutputStream out = new FileOutputStream(dstFile);
	    try
	    {
		if (out != null)
		{
		    out.write(data);
		    out.flush();
		    return true;
		}
	    }
	    finally
	    {
		if (out != null)
		{
		    out.close();
		}
	    }
	}
	return false;
    }

    public static boolean copyFile(File srcFile, File dstFile) throws FileNotFoundException, IOException
    {

	SIPLogger.d(TAG, "copyFile Start from = " + srcFile + " Dest = destDir" + dstFile);
	if (srcFile == null || dstFile == null)
	{
	    return false;
	}
	FileInputStream in = new FileInputStream(srcFile);
	FileOutputStream out = new FileOutputStream(dstFile);
	try
	{
	    if (in != null && out != null)
	    {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bRead = new byte[1024];
		int l = -1;
		if (baos != null)
		{
		    while ((l = in.read(bRead)) != -1)
		    {
			baos.write(bRead, 0, l);
		    }
		    out.write(baos.toByteArray());
		    out.flush();
		    baos.close();
		    SIPLogger.d(TAG, "copyFile End from = " + srcFile + " Dest = destDir" + dstFile);
		    return true;
		}
	    }
	}
	finally
	{
	    if (out != null)
	    {
		out.close();
	    }
	    if (in != null)
	    {
		in.close();
	    }
	}
	SIPLogger.d(TAG, "copyFile Fail End from = " + srcFile + " Dest = destDir" + dstFile);
	return false;
    }

    public static boolean deleteFolder(String path, boolean deleteEntireFolder)
    {

	SIPLogger.d(TAG, "deleteFolder start = " + path + "  DeleteEntireFolder = " + deleteEntireFolder);
	File file = new File(path);
	if (file != null)
	{
	    int i = 0;
	    File[] myFiles = file.listFiles();
	    int length = 0;
	    File temp = null;
	    if (myFiles != null)
	    {
		length = myFiles.length;
	    }
	    while (i < length)
	    {
		do
		{
		    temp = myFiles[i];
		    if (temp == null)
		    {
			break;
		    }
		    if (temp.isDirectory())
		    {
			SIPLogger.d(TAG, "DeleteFolder start " + temp);
			String dirPath = temp.getAbsolutePath();
			deleteFolder(dirPath, true);
			SIPLogger.d(TAG, "DeleteFolder Ends " + temp);
		    }
		    SIPLogger.d(TAG, "Delete File start " + temp);
		    boolean isDeleted = temp.delete();
		    SIPLogger.d(TAG, "Deletion " + (isDeleted ? "success" : "failed"));
		    SIPLogger.d(TAG, "Delete File Ends " + temp);
		}
		while (false);
		i++;
	    }
	    if (deleteEntireFolder)
	    {
		SIPLogger.d(TAG, "Delete Entire Folder started " + path);
		file.delete();
		SIPLogger.d(TAG, "Delete Entire Folder Endeded " + path);
	    }
	    return true;
	}
	return false;
    }

    public static boolean deleteFile(String path)
    {

	SIPLogger.d(TAG, "started deleteFile = " + path);
	File file = new File(path);
	if (file != null)
	{
	    if (file.exists())
	    {
		SIPLogger.d(TAG, "started deleteFile = " + path);
		return file.delete();
	    }
	}
	SIPLogger.d(TAG, "Ends with no delete for  deleteFile = " + path);
	return false;
    }

    public static boolean copyDirectory(String srcPath, String destPath)
    {

	File srcDir = new File(srcPath);
	File destDir = new File(destPath);
	try
	{
	    return copyDirectory(srcDir, destDir);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	return false;
    }

    public static boolean exists(String clPath)
    {

	File file = new File(clPath);
	return file.exists();
    }

    /*
     * This function is used to get the size of folder
     */
    public static long getFileSize(File Path)
    {

	long foldersize = 0;
	try
	{

	    File[] filelist = Path.listFiles();
	    for (int i = 0; i < filelist.length; i++)
	    {
		if (filelist[i].isDirectory())
		{
		    foldersize += getFileSize(filelist[i]);
		}
		else
		{

		    foldersize += filelist[i].length();
		}
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return foldersize;
    }

    public static void copyFiles(File[] listFiles, File clDirPath, boolean srcDelete)
    {

	if (listFiles == null || clDirPath == null)
	{
	    return;
	}
	for (int i = 0; i < listFiles.length; i++)
	{
	    String destFileName = clDirPath.getAbsolutePath() + "/" + listFiles[i].getName();
	    File destFile = new File(destFileName);
	    if (destFile.exists())
	    {
		destFile.delete();
	    }
	    try
	    {
		if (!destFile.exists())
		{
		    destFile.createNewFile();
		}
		copyFile(listFiles[i], destFile);
	    }
	    catch (Exception e)
	    {

	    }
	    finally
	    {
		if (srcDelete)
		{
		    listFiles[i].delete();
		}
	    }
	}
    }

    public static boolean deleteTable(SQLiteDatabase _dbInstance, String table, String whereClause, String[] whereArgs)
    {

	boolean isUpdated = false;
	try
	{
	    if (_dbInstance != null)
	    {
		int deletedCount = _dbInstance.delete(table, whereClause, whereArgs);
		isUpdated = (deletedCount > 0) ? true : false;
		SIPLogger.d("Harish", "Deleted count :::: " + deletedCount);
	    }
	}
	catch (Exception e)
	{
	    // TODO: handle exception
	    // System.out.println("Exception in deleteTable() : " +
	    // e.getMessage());
	    // e.printStackTrace();
	    isUpdated = false;
	}
	return isUpdated;
    }

    public boolean checkTable(SQLiteDatabase _dbInstance, String tableName)
    {

	boolean result = false;
	String query = "SELECT * FROM sqlite_master where tbl_name='" + tableName + "'";
	Cursor cur = _dbInstance.rawQuery(query, null);
	if (cur != null)
	{
	    int rowCount = cur.getCount();
	    if (rowCount > 0)
		result = true;
	    cur.close();
	}
	return result;
    }

    public static String getFileNamePath(String path, String fileName)
    {

	if (path == null)
	{
	    return null;
	}
	File file = new File(path + "/" + fileName);
	if (file.exists())
	{
	    return file.getAbsolutePath();
	}
	return null;

    }

}
