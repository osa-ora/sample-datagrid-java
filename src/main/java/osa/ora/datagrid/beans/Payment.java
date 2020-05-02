package osa.ora.datagrid.beans;

import java.io.Serializable;

public class Payment implements Serializable {

	private static final long serialVersionUID = 1L;
	int account_id;
	String paymentDetails;
	String paymentAddress;
	
	public Payment(int account_id, String paymentDetails, String paymentAddress) {
		this.account_id=account_id;
		this.paymentDetails=paymentDetails;
		this.paymentAddress=paymentAddress;
	}

	public int getAccount_id() {
		return account_id;
	}

	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getPaymentAddress() {
		return paymentAddress;
	}

	public void setPaymentAddress(String paymentAddress) {
		this.paymentAddress = paymentAddress;
	}
	
}
