

export interface VmsTemplate {
    id: number; //JJ change private set; to set;

    EquipType: string;

    Name: string;

    Height: number;

    Width: number;

    Phase: number;

    Pictograms: Array<VmsTemplatePictogram>,

    TextLines: Array<VmsTemplateTextLine>,
}

export function createVmsTemplate(idi, eqtTypei, namei, heighti, widthi, phasei,
    pictogramsi: Array<VmsTemplatePictogram>, textLinesi: Array<VmsTemplateTextLine>): VmsTemplate {
    return {
        id: idi,
        EquipType: eqtTypei,
        Name: namei,
        Height: heighti,
        Width: widthi,
        Phase: phasei,
        Pictograms: pictogramsi,
        TextLines: textLinesi,
    }
}

export interface VmsMsgTextLine {
    // public VmsTemplateTextLine Template { get; set; }

    RichCharCollection: Array<RichCharCollection>;
}
export interface RichCharCollection {
    Text: string;
    Color: string;
}

export interface VmsMsg {
    EquipId: string,

    EquipType: string,

    ExpWayCode: string,

    Phase: number,

    IpAddr: string,

    UpdateTime: Date,

    displayMode: number,

    toggleMode: number,

    VmsMsgPages: Array<VmsMsgPage>,

}
export interface VmsMsgPage {
    Number: number,//page number

    Template: VmsTemplate,

    mode: number,

    toggleTime: number,

    Pictograms: Array<VmsMsgPictogram>,

    TextLines: Array<VmsMsgTextLine>,
}
export interface VmsMsgPictogram {
    Template: VmsTemplatePictogram,

    Pictogram: PictogramInfo,

}

export interface VmsTemplatePictogram {
    Sequence: number,

    Height: number,

    Width: number,

    X: number,

    Y: number,

    DefaultImage: PictogramInfo,

}

export interface PictogramInfo {
    id: number,

    fileName?: string,

    height?: number,

    width?: number,

    size?: number,

    checkSum?: number,

    description?: string,

    groupId?: number,

    image?: any,

    EquipIds?: Array<string>
}
export interface VmsTemplateTextLine {
    Number: number,

    FontInfo: FontType,

    X: number,

    Y: number,

    MaxNumberChar: number,

    CharSpacing: number,

    DefaultText: string,

    Alignment: string,

}
export interface FontType {
    FontHeight: number,
    FontWidth: number,
}


// export interface vmsMsgDto {

//     cmdIdField: string,

//     dateTimeField: Date,

//     dateTimeFieldSpecified: boolean,

//     displayModeField: number,

//     equipIdField: string,

//     equipTypeField: string,

//     execIdField: string,

//     senderField: string,

//     systemIdField: string,

//     toggleModeField,

//     vmsMsgPageDtoListField: Array<vmsMsgPageDto>,
//     cmdId: string,
//     dateTime: Date,
//     dateTimeSpecified: boolean,
//     displayMode: number,
//     equipId: string,
//     equipType: string,
//     execId: string,
//     sender: string,
//     systemId: string,
//     toggleMode: number,
//     vmsMsgPageDtoList: Array<vmsMsgPageDto>,
// }




export interface VmsMsgPageDto {

    graphicContents: any[];
    lineTextDtoList: any[];
    equipId: string;
    id: number;
    pageMode: number;
    pageNo: number;
    toggleTime: number;
    vmsPictogramConfigDtoList: any[];
    vmsPictogramConfigId: string;
    vmsTemplateConfigDto: VmsTemplateConfigDto;
    vmsTemplateId: number;
}

export interface VmsTemplateConfigDto {
    id: number;
    templateId: number;
    equipType: string;
    templateName: string;
    height: number;
    width: number;
    backgroundId: number;
    inhibition: number;
    phase: number;
    vmsTemplatePicConfigDtoList: any[];
    vmsTemplateTextlineConfigDtoList: any[];
}