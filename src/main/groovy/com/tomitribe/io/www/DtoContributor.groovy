package com.tomitribe.io.www

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
class DtoContributor {
    @XmlElement
    String login

    @XmlElement
    String avatarUrl

    @XmlElement
    String name
}