package com.github.yashap.dao

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, BasicAWSCredentials}

class BasicAWSCredentialsProvider(accessKeyId: String, secretAccessKey: String) extends AWSCredentialsProvider {
  override def getCredentials: AWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey)
  override def refresh() = {}
}
