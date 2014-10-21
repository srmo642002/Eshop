
<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:s0="urn:Foo" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="urn:Foo">
      <s:complexType name="ReverseResult">
        <s:sequence>
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="refNum" type="s:string" />
          <s:element minOccurs="1" maxOccurs="1" form="unqualified" name="resultCode" type="s:int" />
          <s:element minOccurs="0" maxOccurs="1" form="unqualified" name="resultDesc" type="s:string" />
        </s:sequence>
      </s:complexType>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="verifyTransactionSoapIn">
    <wsdl:part name="String_1" type="s:string" />
    <wsdl:part name="String_2" type="s:string" />
  </wsdl:message>
  <wsdl:message name="verifyTransactionSoapOut">
    <wsdl:part name="result" type="s:double" />
  </wsdl:message>
  <wsdl:message name="verifyTransaction1SoapIn">
    <wsdl:part name="String_1" type="s:string" />
    <wsdl:part name="String_2" type="s:string" />
  </wsdl:message>
  <wsdl:message name="verifyTransaction1SoapOut">
    <wsdl:part name="result" type="s:double" />
  </wsdl:message>
  <wsdl:message name="reverseTransactionSoapIn">
    <wsdl:part name="String_1" type="s:string" />
    <wsdl:part name="String_2" type="s:string" />
    <wsdl:part name="Username" type="s:string" />
    <wsdl:part name="Password" type="s:string" />
  </wsdl:message>
  <wsdl:message name="reverseTransactionSoapOut">
    <wsdl:part name="result" type="s:double" />
  </wsdl:message>
  <wsdl:message name="reverseTransaction1SoapIn">
    <wsdl:part name="String_1" type="s:string" />
    <wsdl:part name="String_2" type="s:string" />
    <wsdl:part name="Password" type="s:string" />
    <wsdl:part name="Amount" type="s:double" />
  </wsdl:message>
  <wsdl:message name="reverseTransaction1SoapOut">
    <wsdl:part name="result" type="s:double" />
  </wsdl:message>
  <wsdl:message name="reverseTransaction2SoapIn">
    <wsdl:part name="String_1" type="s:string" />
    <wsdl:part name="String_2" type="s:string" />
    <wsdl:part name="Password" type="s:string" />
    <wsdl:part name="Amount" type="s:double" />
  </wsdl:message>
  <wsdl:message name="reverseTransaction2SoapOut">
    <wsdl:part name="result" type="s0:ReverseResult" />
  </wsdl:message>
  <wsdl:portType name="ReferencePayment1Soap">
    <wsdl:operation name="verifyTransaction">
      <wsdl:input message="tns:verifyTransactionSoapIn" />
      <wsdl:output message="tns:verifyTransactionSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="verifyTransaction1">
      <wsdl:input message="tns:verifyTransaction1SoapIn" />
      <wsdl:output message="tns:verifyTransaction1SoapOut" />
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction">
      <wsdl:input message="tns:reverseTransactionSoapIn" />
      <wsdl:output message="tns:reverseTransactionSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction1">
      <wsdl:input message="tns:reverseTransaction1SoapIn" />
      <wsdl:output message="tns:reverseTransaction1SoapOut" />
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction2">
      <wsdl:input message="tns:reverseTransaction2SoapIn" />
      <wsdl:output message="tns:reverseTransaction2SoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="ReferencePayment1Soap" type="tns:ReferencePayment1Soap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc" />
    <wsdl:operation name="verifyTransaction">
      <soap:operation soapAction="verifyTransaction" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="verifyTransaction1">
      <soap:operation soapAction="" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction">
      <soap:operation soapAction="reverseTransaction" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction1">
      <soap:operation soapAction="reverseTransaction1" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction2">
      <soap:operation soapAction="reverseTransaction2" style="rpc" />
      <wsdl:input>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="encoded" namespace="urn:Foo" encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="ReferencePayment1Soap12" type="tns:ReferencePayment1Soap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc" />
    <wsdl:operation name="verifyTransaction">
      <soap12:operation soapAction="verifyTransaction" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="verifyTransaction1">
      <soap12:operation soapAction="" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction">
      <soap12:operation soapAction="reverseTransaction" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction1">
      <soap12:operation soapAction="reverseTransaction1" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="reverseTransaction2">
      <soap12:operation soapAction="reverseTransaction2" style="rpc" />
      <wsdl:input>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="encoded" namespace="urn:Foo" encodingStyle="http://www.w3.org/2003/05/soap-encoding" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="ReferencePayment1">
    <wsdl:port name="ReferencePayment1Soap" binding="tns:ReferencePayment1Soap">
      <soap:address location="https://sep.shaparak.ir/payments/referencepayment.asmx" />
    </wsdl:port>
    <wsdl:port name="ReferencePayment1Soap12" binding="tns:ReferencePayment1Soap12">
      <soap12:address location="https://sep.shaparak.ir/payments/referencepayment.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>